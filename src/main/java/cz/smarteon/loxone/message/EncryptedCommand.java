package cz.smarteon.loxone.message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * {@link LoxoneMessageCommand} which is send over encrypted.
 * @param <V> value type of the command
 */
public class EncryptedCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String ENC_PREFIX = "jdev/sys/enc/";

    private final Function<String, String> encryptor;

    /**
     * Creates new instance
     * @param command loxone command (before encryption)Token
     * @param valueType type of command response
     * @param encryptor function to be used for encryption
     */
    protected EncryptedCommand(final String command, final Class<V> valueType, final Function<String, String> encryptor) {
        super(command, Type.JSON, valueType, false, true);
        this.encryptor = requireNonNull(encryptor, "encryptor can't be null");
    }

    /**
     * Encrypted command with prefix identifying it's encrypted
     * @return encrypted command
     */
    @Override
    public String getCommand() {
        return ENC_PREFIX + encodeUrl(encryptor.apply(super.getCommand()));
    }

    /**
     * Plain command before encryption.
     * @return command without encryption applied
     */
    public String getDecryptedCommand() {
        return super.getCommand();
    }

    /**
     * Creates "authwithtoken" command
     * @param tokenHash hashed token
     * @param user loxone user
     * @param encryptor encryption function
     * @return command
     */
    public static EncryptedCommand<Token> authWithToken(final String tokenHash, final String user,
                                                        final Function<String, String> encryptor) {
        final String cmd = "authwithtoken/"
                + requireNonNull(tokenHash, "tokenHash can't be null") + "/"
                + requireNonNull(user, "user can't be null");
        return new EncryptedCommand<>(cmd, Token.class, encryptor);
    }

    /**
     * Creates "gettoken" command
     * @param tokenHash hashed token
     * @param user loxone userauthwithtoken
     * @param clientUuid loxone client uuid
     * @param clientInfo  loxone client info
     * @param encryptor encryption function
     * @return command
     */
    public static EncryptedCommand<Token> getToken(final String tokenHash, final String user, final String clientUuid,
                                                   final String clientInfo, final Function<String, String> encryptor) {
        final String cmd = "jdev/sys/gettoken/"
                + requireNonNull(tokenHash, "tokenHash can't be null") + "/"
                + requireNonNull(user, "user can't be null") + "/2/"
                + requireNonNull(clientUuid, "clientUuid can't be null") + "/"
                + requireNonNull(clientInfo, "clientInfo can't be null");
        return new EncryptedCommand<>(cmd, Token.class, encryptor);
    }

    /**
     * Creates "refreshtoken" command
     * @param tokenHash hashed token
     * @param user loxone user
     * @param encryptor encryption function
     * @return command
     */
    public static EncryptedCommand<Token> refreshToken(final String tokenHash, final String user,
                                                        final Function<String, String> encryptor) {
        final String cmd = "jdev/sys/refreshtoken/"
                + requireNonNull(tokenHash, "tokenHash can't be null") + "/"
                + requireNonNull(user, "user can't be null");
        return new EncryptedCommand<>(cmd, Token.class, encryptor);
    }

    private static String encodeUrl(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding should be present everywhere", e);
        }
    }
}
