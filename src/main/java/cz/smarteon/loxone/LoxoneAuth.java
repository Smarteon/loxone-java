package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ApiInfo;
import cz.smarteon.loxone.message.EncryptedCommand;
import cz.smarteon.loxone.message.Hashing;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.LoxoneValue;
import cz.smarteon.loxone.message.PubKeyInfo;
import cz.smarteon.loxone.message.Token;
import org.java_websocket.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.concatToBytes;
import static cz.smarteon.loxone.Command.keyExchange;
import static cz.smarteon.loxone.Protocol.isCommandGetVisuSalt;
import static cz.smarteon.loxone.message.LoxoneMessageCommand.getKey;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

/**
 * Encapsulates algorithms necessary to perform loxone authentication with loxone server version 9.
 * First the {@link #init()} should be called, then the other methods work correctly.
 *
 * @see <a href="https://www.loxone.com/dede/wp-content/uploads/sites/2/2016/08/0900_Communicating-with-the-Miniserver.pdf">Loxone communication</a>
 */
public class LoxoneAuth implements CommandListener {

    /**
     * UUID of this client sent as part of token request
     */
    public static final String CLIENT_UUID = "5231fc55-a384-41b4-b0ae10b7f774add1";

    /**
     * Default value of client info sent as part of token request
     */
    public static final String DEFAULT_CLIENT_INFO = "loxoneJava";

    private static final Logger log = LoggerFactory.getLogger(LoxoneAuth.class);

    private static final int MAX_SALT_USAGE = 20;

    private final LoxoneHttp loxoneHttp;
    private final String loxoneUser;
    private final String loxonePass;
    private final String loxoneVisPass;

    private ApiInfo apiInfo;
    private PublicKey publicKey;
    private SecretKey sharedKey;
    private byte[] sharedKeyIv;
    private Hashing visuHashing;
    private Token token;

    private String sharedSalt;
    private int saltUsageCount = 0;
    private SecureRandom sha1PRNG;

    private String clientInfo = DEFAULT_CLIENT_INFO;

    private final LoxoneMessageCommand<Hashing> getKeyCommand;
    private EncryptedCommand<Token> lastTokenCommand;

    private final List<AuthListener> authListeners;
    private CommandSender commandSender;


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

        this.getKeyCommand = getKey(loxoneUser);

        this.authListeners = new LinkedList<>();
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
     * @return UUID of loxone-java client (currently hardcoded to {@link #CLIENT_UUID})
     */
    public String getUuid() {
        return CLIENT_UUID;
    }

    /**
     * @return clientInfo sent as part of token request
     */
    public String getClientInfo() {
        return clientInfo;
    }


    /**
     * Sets the client info sent as part of token request, defaults to {@link #DEFAULT_CLIENT_INFO}
     * @param clientInfo client info
     */
    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public void setCommandSender(final CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    /**
     * Initialize the loxone authentication. Fetches the API info (address and version) and prepare the cryptography.
     */
    public void init() {
        log.trace("LoxoneAuth init start");
        fetchApiInfo();
        fetchPublicKey();

        sha1PRNG = LoxoneCrypto.getSecureRandom();

        if (sharedKey == null) {
            sharedKey = LoxoneCrypto.createSharedKey();
        }
        sharedKeyIv = LoxoneCrypto.createSharedKeyIv(sha1PRNG);

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
    private String getSessionKey() {
        checkInitialized();
        return LoxoneCrypto.createSessionKey(sharedKey, sharedKeyIv, publicKey);
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

    public boolean isUsable() {
        return new TokenState(token).isUsable();
    }

    public void startAuthentication() {
        sendCommand(keyExchange(getSessionKey())); // TODO is necessary to recreate the session key everytime?
        sendCommand(getKeyCommand);
    }

    @Override
    public State onCommand(String command, LoxoneValue value) {
        if (getKeyCommand.is(command)) {
            final Hashing hashing = getKeyCommand.ensureValue(value);
            final TokenState tokenState = new TokenState(token);
            if (tokenState.isExpired()) {
                lastTokenCommand = EncryptedCommand.getToken(
                        LoxoneCrypto.loxoneHashing(loxonePass, loxoneUser, hashing, "gettoken"),
                        loxoneUser, CLIENT_UUID, clientInfo, this::encryptCommand
                );
            } else if (tokenState.needsRefresh()) {
                lastTokenCommand = EncryptedCommand.refreshToken(
                        LoxoneCrypto.loxoneHashing(token.getToken(), loxoneUser, hashing, "refreshtoken"),
                        loxoneUser, this::encryptCommand
                );
            } else {
                lastTokenCommand = EncryptedCommand.authWithToken(
                        LoxoneCrypto.loxoneHashing(token.getToken(), loxoneUser, hashing, "authwithtoken"),
                        loxoneUser, this::encryptCommand
                );
            }
            sendCommand(lastTokenCommand);
            return State.CONSUMED;
        } else if (isCommandGetVisuSalt(command, loxoneUser)) {
            visuHashing = parseHashing(value);
            return visuHashing != null ? State.CONSUMED : State.IGNORED;
        } else if (lastTokenCommand != null && lastTokenCommand.is(command)) {
            token = lastTokenCommand.ensureValue(value);
            authListeners.forEach(AuthListener::authCompleted);
            lastTokenCommand = null;
            return State.CONSUMED;
        }

        return State.IGNORED;
    }

    public void registerAuthListener(final AuthListener listener) {
        authListeners.add(listener);
    }


    private void sendCommand(final Command command) {
        if (commandSender != null) {
            commandSender.send(command);
        } else {
            throw new IllegalStateException("CommandSender not set, authentication cannot work correctly");
        }
    }

    /**
     * Encrypts the given command, returning encrypted command ready to be sent to loxone, may throw exception if not properly initialized.
     * @param command command to be encrypted
     * @return new command which carries the given command encrypted
     */
    private String encryptCommand(String command) {
        if (sharedSalt == null) {
            sharedSalt = LoxoneCrypto.generateSalt(sha1PRNG);
        }
        String saltPart = "salt/" + sharedSalt;
        if (isNewSaltNeeded()) {
            log.trace("changing the salt");
            saltPart = "nextSalt/" + sharedSalt + "/";
            sharedSalt = LoxoneCrypto.generateSalt(sha1PRNG);;
            saltPart += sharedSalt;
        }

        return encryptWithSharedKey(saltPart + "/" + command);
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
            final LoxoneMessage<ApiInfo> msg = loxoneHttp.get(LoxoneMessageCommand.DEV_CFG_API);
            if (msg.getValue() != null) {
                apiInfo = msg.getValue();
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
            final LoxoneMessage<PubKeyInfo> msg = loxoneHttp.get(LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY);
            if (msg.getValue() != null) {
                publicKey = msg.getValue().asPublicKey();
            } else {
                throw new LoxoneException("Got empty pubKeyInfo");
            }
        } finally {
            log.trace("Fetching PublicKey finish");
        }
    }

    private String encryptWithSharedKey(final String data) {
        checkInitialized();
        return LoxoneCrypto.encrypt(data, sharedKey, sharedKeyIv);
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
}