package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ApiInfo;
import cz.smarteon.loxone.message.Hashing;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.PubKeyInfo;
import org.java_websocket.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import static java.util.Objects.requireNonNull;

/**
 * Encapsulates algorithms necessary to perform loxone authentication with loxone server version 9.
 * First the {@link #init()} should be called, then the other methods work correctly.
 *
 * @see <a href="https://www.loxone.com/dede/wp-content/uploads/sites/2/2016/08/0900_Communicating-with-the-Miniserver.pdf">Loxone communication</a>
 */
public class LoxoneAuth implements CommandListener {

    private static final Logger log = LoggerFactory.getLogger(LoxoneAuth.class);

    private static final int MAX_SALT_USAGE = 20;
    private static final String CLIENT_UUID = "5231fc55-a384-41b4-b0ae10b7f774add1";

    private final LoxoneHttp loxoneHttp;
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

    /**
     * Creates new instance
     * @param loxoneHttp loxone http interface used to perform some necessary http calls to loxone
     * @param loxoneUser loxone user
     * @param loxonePass loxone password
     * @param loxoneVisPass loxone visualization password
     */
    public LoxoneAuth(LoxoneHttp loxoneHttp, String loxoneUser, String loxonePass, String loxoneVisPass) {
        this.loxoneHttp = requireNonNull(loxoneHttp, "loxoneHttp shouldn't be null");
        this.loxoneUser = requireNonNull(loxoneUser, "loxoneUser shouldn't be null");
        this.loxonePass = requireNonNull(loxonePass, "loxonePass shouldn't be null");
        this.loxoneVisPass = requireNonNull(loxoneVisPass, "loxoneVisPass shouldn't be null");
    }

    /**
     * @return loxone {@link ApiInfo}, or null if not properly initialized
     */
    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    /**
     * @return loxone user
     */
    public String getUser() {
        return loxoneUser;
    }

    /**
     * @return UUID of loxone-java client (currently hardcoded)
     */
    public String getUuid() {
        return CLIENT_UUID;
    }

    /**
     * Initialize the loxone authentication. Fetches the API info (address and version) and prepare the cryptography.
     */
    public void init() {
        log.trace("LoxoneAuth init start");
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

        log.trace("LoxoneAuth init finish");
    }

    /**
     * @return true if properly initialized, false otherwise
     */
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
     * Returns RSA encrypted generated shared key prepared for key exchange. May throw an exception if not initialized properly.
     * @return RSA encrypted sharedKey
     */
    public String getSessionKey() {
        checkInitialized();
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedbytes = cipher.doFinal(concat(sharedKey.getEncoded(), sharedKeyIv));
            log.trace("Created session key (in hex): {}", bytesToHex(encryptedbytes));
            return Base64.encodeBytes(encryptedbytes);
        } catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new LoxoneException("Can't encrypt sharedKey to obtain sessionKey", e);
        }
    }

    /**
     * Creates gettoken command, implies the hashing has been received from loxone server recently using get key command.
     * @return new gettoken command
     */
    public String getTokenCommand() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] toSha1 = concatToBytes(loxonePass, hashing.getSalt());
            final String pwHash = bytesToHex(md.digest(toSha1)).toUpperCase();
            log.trace("getToken password hash: {}", pwHash);

            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec secret = new SecretKeySpec(hashing.getKey(), "HmacSHA1");
            mac.init(secret);
            final byte[] hash = mac.doFinal(concatToBytes(loxoneUser, pwHash));
            final String finalHash = bytesToHex(hash);
            log.trace("getToken final hash: {}", finalHash);

            return jsonGetToken(finalHash, loxoneUser, CLIENT_UUID, "smarteonAndroid");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform hashing to prepare gettoken command", e);
        }
    }

    /**
     * Encrypts the given command, returning encrypted command ready to be sent to loxone, may throw exception if not properly initialized.
     * @param command command to be encrypted
     * @return new command which carries the given command encrypted
     */
    public String encryptCommand(String command) {
        if (sharedSalt == null) {
            sharedSalt = generateSalt();
        }
        String saltPart = "salt/" + sharedSalt;
        if (isNewSaltNeeded()) {
            log.trace("changing the salt");
            saltPart = "nextSalt/" + sharedSalt + "/";
            sharedSalt = generateSalt();
            saltPart += sharedSalt;
        }

        return jsonEncrypted(encryptWithSharedKey(saltPart + "/" + command));
    }

    /**
     * Computes visualization hash, which can be used in secured command, implies that visualization hashing has been obtained
     * recently using getvisusalt command
     * @return visualization hash
     *
     */
    public String getVisuHash() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] toSha1 = concatToBytes(loxoneVisPass, visuHashing.getSalt());
            final String pwHash = bytesToHex(md.digest(toSha1)).toUpperCase();
            log.trace("visuPassHash: {}", pwHash);

            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec secret = new SecretKeySpec(visuHashing.getKey(), "HmacSHA1");
            mac.init(secret);
            final byte[] hash = mac.doFinal(pwHash.getBytes());
            final String finalHash = bytesToHex(hash);
            log.trace("visuPass final hash: {}", finalHash);

            return finalHash;
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
        } else {
            log.warn("Unexpected type of hashing received from loxone: {}", value.getClass());
            return null;
        }
    }

    private void checkInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("LoxoneAuth has not been initialized - call init() first");
        }
    }

    private void fetchApiInfo() {
        log.trace("Fetching ApiInfo start");
        try {
            final LoxoneMessage msg = loxoneHttp.get(Protocol.C_JSON_API);
            if (msg.getValue() != null) {
                if (msg.getValue() instanceof ApiInfo) {
                    apiInfo = (ApiInfo) msg.getValue();
                } else {
                    throw new LoxoneException("Unexpected apiInfo message type " + msg.getValue().getClass());
                }
            } else {
                throw new LoxoneException("Got empty apiInfo");
            }
        } finally {
            log.trace("Fetching ApiInfo finish");
        }
    }

    private void fetchPublicKey() {
        log.trace("Fetching PublicKey start");
        try {
            final LoxoneMessage msg = loxoneHttp.get(Protocol.C_JSON_PUBLIC_KEY);
            if (msg.getValue() != null) {
                if (msg.getValue() instanceof PubKeyInfo) {
                    publicKey = ((PubKeyInfo) msg.getValue()).asPublicKey();
                } else {
                    throw new LoxoneException("Unexpected pubKeyInfo message type " + msg.getValue().getClass());
                }
            } else {
                throw new LoxoneException("Got empty pubKeyInfo");
            }
        } finally {
            log.trace("Fetching PublicKey finish");
        }
    }

    private void createSharedKey() {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            sharedKey = keyGen.generateKey();
            log.trace("Created sharedKey: {}", bytesToHex(sharedKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No AES provider present", e);

        }
    }

    private void createSharedKeyIv() {
        sharedKeyIv = new byte[16];
        sha1PRNG.nextBytes(sharedKeyIv);
        log.trace("Created sharedKeyIv: {}", bytesToHex(sharedKeyIv));
    }

    private String encryptWithSharedKey(String data) {
        checkInitialized();
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
        final String salt = bytesToHex(iv);
        log.trace("new command encryption salt generated: {}", salt);
        return salt;
    }
}