package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ApiInfo;
import cz.smarteon.loxone.message.EncryptedCommand;
import cz.smarteon.loxone.message.Hashing;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.PubKeyInfo;
import cz.smarteon.loxone.message.Token;
import cz.smarteon.loxone.message.TokenPermissionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static cz.smarteon.loxone.Codec.bytesToBase64;
import static cz.smarteon.loxone.Codec.concatToBytes;
import static cz.smarteon.loxone.Command.keyExchange;
import static cz.smarteon.loxone.message.LoxoneMessageCommand.getKey;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

/**
 * Encapsulates algorithms necessary to perform loxone authentication with loxone server version 10.
 * First the {@link #init()} should be called, then the other methods work correctly.
 *
 * @see <a href="https://www.loxone.com/enen/wp-content/uploads/sites/3/2016/10/1000_Communicating-with-the-Miniserver.pdf">Loxone communication</a>
 */
public class LoxoneAuth implements CommandResponseListener<LoxoneMessage<?>> {

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
    private final LoxoneProfile profile;

    private final LoxoneMessageCommand<Hashing> getKeyCommand;
    private final LoxoneMessageCommand<Hashing> getVisuHashCommand;
    private final List<AuthListener> authListeners;

    // Crypto stuff
    private ApiInfo apiInfo;
    private PublicKey publicKey;
    private SecretKey sharedKey;
    private SecureRandom sha1PRNG;
    private byte[] sharedKeyIv;
    private String sharedSalt;
    private int saltUsageCount = 0;

    // Communication stuff
    private Hashing visuHashing;
    private Token token;
    private TokenStateEvaluator tokenStateEvaluator = new TokenStateEvaluator() {};

    private String clientInfo = DEFAULT_CLIENT_INFO;
    private TokenPermissionType tokenPermissionType = TokenPermissionType.WEB;

    private EncryptedCommand<Token> lastTokenCommand;

    private CommandSender commandSender;

    private boolean autoRefreshToken = false;
    private ScheduledExecutorService autoRefreshScheduler;
    private ScheduledFuture<?> autoRefreshFuture;

    /**
     * Creates new instance
     * @param loxoneHttp loxone http interface used to perform some necessary http calls to loxone
     * @param profile loxone profile
     */
    public LoxoneAuth(@NotNull LoxoneHttp loxoneHttp, @NotNull LoxoneProfile profile) {
        this.loxoneHttp = requireNonNull(loxoneHttp, "loxoneHttp shouldn't be null");
        this.profile = requireNonNull(profile, "profile shouldn't be null");

        this.getKeyCommand = getKey(profile.getUsername());
        this.getVisuHashCommand = LoxoneMessageCommand.getVisuHash(profile.getUsername());

        this.authListeners = new LinkedList<>();
    }

    @Deprecated
    public LoxoneAuth(@NotNull LoxoneHttp loxoneHttp, @NotNull String loxoneUser, @NotNull String loxonePass,
                      @Nullable String loxoneVisPass) {
        this(loxoneHttp, new LoxoneProfile(loxoneHttp.endpoint, loxoneUser, loxonePass, loxoneVisPass));
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
        return profile.getUsername();
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
     * Allows to modify client info sent as part of token request, defaults to {@link #DEFAULT_CLIENT_INFO}
     * @param clientInfo client info
     */
    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * @return used token permission type
     */
    public TokenPermissionType getTokenPermissionType() {
        return tokenPermissionType;
    }

    /**
     * Token permission type used to acquire token. Defaults to {@link TokenPermissionType#WEB}
     * @param tokenPermissionType token permission type
     */
    public void setTokenPermissionType(final TokenPermissionType tokenPermissionType) {
        this.tokenPermissionType = tokenPermissionType;
    }

    /**
     * Set the command sender which allows to send commands over websocket to miniserver. It must be set, otherwise this
     * class cannot work.
     * @param commandSender command sender.
     */
    public void setCommandSender(final CommandSender commandSender) {
        this.commandSender = requireNonNull(commandSender, "commandSender can't be null");
    }

    /**
     * Whether is configured to refresh token automatically
     * @return true if is configured to automatically refresh token, false (default) otherwise
     */
    public boolean isAutoRefreshToken() {
        return autoRefreshToken;
    }

    /**
     * Allow or disallow to automatically refresh token. Disabled by default. If set to true the token refresh will
     * be scheduled after next token receive. If se to false, when previously true, only next token refresh are prevented,
     * not the currently scheduled one.
     * @param autoRefreshToken whether to automatically refresh token
     */
    public void setAutoRefreshToken(final boolean autoRefreshToken) {
        this.autoRefreshToken = autoRefreshToken;
    }

    /**
     * Allows to specify scheduler used to auto refresh tokens. If not set the new single thread scheduler is created internally.
     * @see #setAutoRefreshToken(boolean)
     * @param autoRefreshScheduler scheduler use to refresh tokens
     */
    public void setAutoRefreshScheduler(final @NotNull ScheduledExecutorService autoRefreshScheduler) {
        this.autoRefreshScheduler = requireNonNull(autoRefreshScheduler, "autoRefreshScheduler can't be null");
    }

    void setTokenStateEvaluator(final TokenStateEvaluator tokenStateEvaluator) {
        this.tokenStateEvaluator = tokenStateEvaluator;
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
        return singletonMap("Authorization", "Basic " + bytesToBase64(concatToBytes(profile.getUsername(), profile.getPassword())));
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
        return onVisuPassSet("compute visu hash",
                () ->  LoxoneCrypto.loxoneHashing(profile.getVisuPassword(), null, visuHashing, "secured command") );
    }

    /**
     * Checks whether this authentication is in usable state - in terms to be used for authorized commands. In case
     * it returns false the authentication must be started over using {@link #startAuthentication()}.
     * @return true if the connection is authenticated, false otherwise
     */
    boolean isUsable() {
        return tokenStateEvaluator.evaluate(token).isUsable();
    }

    /**
     * Starts the authentication mechanism.
     */
    void startAuthentication() {
        authListeners.forEach(AuthListener::beforeAuth);
        sendCommand(keyExchange(getSessionKey())); // TODO is necessary to recreate the session key everytime?
        sendCommand(getKeyCommand);
    }

    void startVisuAuthentication() {
        authListeners.forEach(AuthListener::beforeVisuAuth);
        onVisuPassSet("start visual authentication", () -> {
            sendCommand(getVisuHashCommand);
            return null;
        });
    }

    /**
     * Processes all authentication related incoming commands
     * @param command command to process
     * @param message message to process
     * @return state
     */
    @Override @NotNull
    public State onCommand(@NotNull final Command<? extends LoxoneMessage<?>> command, @NotNull final LoxoneMessage<?> message) {
        if (getKeyCommand.equals(command)) {
            final Hashing hashing = getKeyCommand.ensureValue(message.getValue());
            final TokenState tokenState = tokenStateEvaluator.evaluate(token);
            if (tokenState.isExpired()) {
                lastTokenCommand = EncryptedCommand.getToken(
                        LoxoneCrypto.loxoneHashing(profile.getPassword(), profile.getUsername(), hashing, "gettoken"),
                        profile.getUsername(), tokenPermissionType, CLIENT_UUID, clientInfo, this::encryptCommand
                );
            } else if (tokenState.needsRefresh()) {
                lastTokenCommand = EncryptedCommand.refreshToken(
                        LoxoneCrypto.loxoneHashing(token.getToken(), hashing, "refreshtoken"),
                        profile.getUsername(), this::encryptCommand
                );
            } else {
                lastTokenCommand = EncryptedCommand.authWithToken(
                        LoxoneCrypto.loxoneHashing(token.getToken(), hashing, "authwithtoken"),
                        profile.getUsername(), this::encryptCommand
                );
            }
            sendCommand(lastTokenCommand);
            return State.CONSUMED;
        } else if (getVisuHashCommand.equals(command)) {
            visuHashing = getVisuHashCommand.ensureValue(message.getValue());
            authListeners.forEach(AuthListener::visuAuthCompleted);
            return State.CONSUMED;
        } else if (lastTokenCommand != null && lastTokenCommand.equals(command)) {
            token = lastTokenCommand.ensureValue(message.getValue());
            log.info("Got loxone token, valid until: " + token.getValidUntilDateTime() + ", seconds to expire: " + token.getSecondsToExpire());

            if (autoRefreshToken) {
                final long secondsToRefresh = tokenStateEvaluator.evaluate(token).secondsToRefresh();
                if (secondsToRefresh > 0) {
                    if (autoRefreshScheduler != null) {
                        log.info("Scheduling token auto refresh in " + secondsToRefresh + " seconds");
                        autoRefreshFuture = autoRefreshScheduler.schedule(this::startAuthentication, secondsToRefresh, TimeUnit.SECONDS);
                    } else {
                        log.warn("autoRefreshScheduler not set, can't schedule token refresh");
                    }
                } else {
                    log.warn("Can't schedule token auto refresh, token expires too early or is already expired");
                }
            }

            authListeners.forEach(AuthListener::authCompleted);
            lastTokenCommand = null;
            return State.CONSUMED;
        }

        return State.IGNORED;
    }

    @Override
    public boolean accepts(@NotNull final Class clazz) {
        return LoxoneMessage.class.isAssignableFrom(clazz);
    }

    /**
     * Registers {@link AuthListener} to notify it about authentication events.
     * @param listener listener to register
     */
    public void registerAuthListener(final AuthListener listener) {
        authListeners.add(listener);
    }

    /**
     * Allows to tear down when websocket is closed.
     */
    void wsClosed() {
        if (autoRefreshFuture != null) {
            autoRefreshFuture.cancel(true);
        }
    }

    private void sendCommand(final Command<?> command) {
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

    private void checkInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("LoxoneAuth has not been initialized - call init() first");
        }
    }

    private void fetchApiInfo() {
        log.trace("Fetching ApiInfo start");
        try {
            final LoxoneMessage<ApiInfo> msg = loxoneHttp.get(LoxoneMessageCommand.DEV_CFG_API);
            msg.getValue();
            apiInfo = msg.getValue();
        } finally {
            log.trace("Fetching ApiInfo finish");
        }
    }

    private void fetchPublicKey() {
        log.trace("Fetching PublicKey start");
        try {
            final LoxoneMessage<PubKeyInfo> msg = loxoneHttp.get(LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY);
            msg.getValue();
            publicKey = msg.getValue().asPublicKey();
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

    private <T> T onVisuPassSet(String actionDescription, Supplier<T> action) {
        if (profile.getVisuPassword() != null) {
            return action.get();
        } else {
            throw new IllegalStateException("Can't " + actionDescription + " when visualization password not set.");
        }
    }
}
