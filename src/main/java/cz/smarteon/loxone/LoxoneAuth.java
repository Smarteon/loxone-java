package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ApiInfo;
import cz.smarteon.loxone.message.Hashing;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.PubKeyInfo;
import org.java_websocket.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.concat;
import static cz.smarteon.loxone.Codec.concatToBytes;
import static cz.smarteon.loxone.Protocol.isCommandGetVisuSalt;
import static cz.smarteon.loxone.Protocol.jsonEncrypted;
import static cz.smarteon.loxone.Protocol.jsonGetKey;
import static cz.smarteon.loxone.Protocol.jsonGetToken;
import static java.util.Collections.singletonMap;

/**
 * Encapsulates algorithms necessary to perform loxone authentication.
 */
public class LoxoneAuth implements CommandListener {



    private static final int MAX_SALT_USAGE = 20;
    private static final String CLIENT_UUID = "5231fc55-a384-41b4-b0ae10b7f774add1";

    private final Protocol protocol;
    private final String loxoneUser;
    private final String loxonePass;
    private final String loxoneVisPass;

    private ApiInfo apiInfo;
    private Hashing hashing;
    private PublicKey publicKey;
    private SecretKey sharedKey;
    private byte[] sharedKeyIv;
    private Hashing visuHashing;

    private String sharedSalt;
    private int saltUsageCount = 0;
    private SecureRandom sha1PRNG;

    public LoxoneAuth(Protocol protocol, String loxoneUser, String loxonePass, String loxoneVisPass) {
        this.protocol = protocol;
        this.loxoneUser = loxoneUser;
        this.loxonePass = loxonePass;
        this.loxoneVisPass = loxoneVisPass;
    }

    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    public String getUser() {
        return loxoneUser;
    }

    public String getUuid() {
        return CLIENT_UUID;
    }

    /**
     * Initialize the loxone authentication. Fetches the API info (address and version) and prepare the cryptography.
     */
    public void init() {
        fetchApiInfo();
        fetchPublicKey();

        try {
            sha1PRNG = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No SHA1PRNG provider present", e);
        }

        if (sharedKey == null) {
            createSharedKey();
        }
        createSharedKeyIv();
    }

    boolean isInitialized() {
        return publicKey != null && sharedKey != null && sharedKeyIv != null && sha1PRNG != null;
    }

    /**
     * @return headers necessary for authentication of HTTP connection
     */
    public Map<String, String> authHeaders() {
        return singletonMap("Authorization", "Basic " + Base64.encodeBytes(concatToBytes(loxoneUser, loxonePass)));
    }

    /**
     * @return encrypted sharedKey
     */
    public String getSessionKey() {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedbytes = cipher.doFinal(concat(sharedKey.getEncoded(), sharedKeyIv));
            return Base64.encodeBytes(encryptedbytes);
        } catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new LoxoneException("Can't encrypt sharedKey to obtain sessionKey", e);
        }
    }

    /**
     * @return new gettoken command
     */
    public String getTokenCommand() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] toSha1 = concatToBytes(loxonePass, hashing.getSalt());
            final String pwHash = bytesToHex(md.digest(toSha1)).toUpperCase();

            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec secret = new SecretKeySpec(hashing.getKey(), "HmacSHA1");
            mac.init(secret);
            final byte[] hash = mac.doFinal(concatToBytes(loxoneUser, pwHash));

            return jsonGetToken(bytesToHex(hash), loxoneUser, CLIENT_UUID, "smarteonAndroid");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform hashing to prepare gettoken command", e);
        }
    }

    /**
     * @param command command to be encrypted
     * @return new command which carries the given command encrypted
     */
    public String encryptCommand(String command) {
        if (sharedSalt == null) {
            sharedSalt = generateSalt();
        }
        String saltPart = "salt/" + sharedSalt;
        if (isNewSaltNeeded()) {
            saltPart = "nextSalt/" + sharedSalt + "/";
            sharedSalt = generateSalt();
            saltPart += sharedSalt;
        }

        return jsonEncrypted(encryptWithSharedKey(saltPart + "/" + command));
    }

    public String getVisuHash() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] toSha1 = concatToBytes(loxoneVisPass, visuHashing.getSalt());
            final String pwHash = bytesToHex(md.digest(toSha1)).toUpperCase();

            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec secret = new SecretKeySpec(visuHashing.getKey(), "HmacSHA1");
            mac.init(secret);
            final byte[] hash = mac.doFinal(pwHash.getBytes());

            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform hashing to prepare visuHash", e);
        }
    }

    @Override
    public State onCommand(String command, Object value) {
        if (jsonGetKey(loxoneUser).equals(command)) {
            hashing = parseHashing(value);
            return hashing != null ? State.CONSUMED : State.IGNORED;
        } else if (isCommandGetVisuSalt(command, loxoneUser)) {
            visuHashing = parseHashing(value);
            return visuHashing != null ? State.CONSUMED : State.IGNORED;
        }

        return State.IGNORED;
    }

    private Hashing parseHashing(Object value) {
        if (value instanceof Hashing) {
            return (Hashing) value;
        }
        return null;
    }

    private void checkInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("LoxoneAuth has not been initialized - call init() first");
        }
    }

    private void fetchApiInfo() {
        final LoxoneMessage msg = LoxoneHttp.get(protocol.api());
        if (msg.getValue() != null) {
            if (msg.getValue() instanceof ApiInfo) {
                apiInfo = (ApiInfo) msg.getValue();
            } else {
                throw new LoxoneException("Unexpected apiInfo message type " + msg.getValue().getClass());
            }
        } else {
            throw new LoxoneException("Got empty apiInfo");
        }
    }

    private void fetchPublicKey() {
        final LoxoneMessage msg = LoxoneHttp.get(protocol.publicKey());
        if (msg.getValue() != null) {
            if (msg.getValue() instanceof PubKeyInfo) {
                publicKey = ((PubKeyInfo) msg.getValue()).asPublicKey();
            } else {
                throw new LoxoneException("Unexpected pubKeyInfo message type " + msg.getValue().getClass());
            }
        } else {
            throw new LoxoneException("Got empty pubKeyInfo");
        }
    }

    private void createSharedKey() {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            sharedKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No AES provider present", e);

        }
    }

    private void createSharedKeyIv() {
        sharedKeyIv  = new byte[16];
        sha1PRNG.nextBytes(sharedKeyIv);
    }

    private String encryptWithSharedKey(String data) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey, ivspec);
            return Base64.encodeBytes(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform AES encryption", e);
        }
    }

    private String decryptWithSharedKey(String data) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.DECRYPT_MODE, sharedKey, ivspec);
            return new String(cipher.doFinal(Base64.decode(data)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            throw new LoxoneException("Can't perform AES decryption", e);
        }
    }

    /**
     * It is/isn't necessary to create new salt based on MAX_SALT_USAGE or the timestamp
     * ​“nextSalt/{prevSalt}/{nextSalt}/{cmd} sent with command
     *
     * return true/false should/shouldn't create new salt.
     * */
    private boolean isNewSaltNeeded() {
        if (saltUsageCount <= 0) {
            //TODO update sharedSalt every hour
        }
        saltUsageCount++;
        if (saltUsageCount >= MAX_SALT_USAGE) {
            saltUsageCount = 0;
            return true;
        }
        return false;
    }

    /**
     * Generates a random shared salt used for command encryption.
     * Hex​ ​string​ ​(length​ ​may​ ​vary,​ ​e.g.​ ​2​ ​bytes)​ ​->​ ​{salt}
     * */
    private String generateSalt() {
        byte[] iv = new byte[16];
        sha1PRNG.nextBytes(iv);
        return bytesToHex(iv);
    }
}