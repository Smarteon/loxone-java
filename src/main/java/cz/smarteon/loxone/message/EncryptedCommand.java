package cz.smarteon.loxone.message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class EncryptedCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String ENC_PREFIX = "jdev/sys/enc/";

    private final Function<String, String> encryptor;

    protected EncryptedCommand(final String command, final Class<V> valueType, final Function<String, String> encryptor) {
        super(command, Type.JSON, valueType, false, true);
        this.encryptor = requireNonNull(encryptor);
    }

    @Override
    public String getCommand() {
        return ENC_PREFIX + encodeUrl(encryptor.apply(super.getCommand()));
    }

    public static EncryptedCommand<Token> authWithToken(final String tokenHash, final String user,
                                                        final Function<String, String> encryptor) {
        final String cmd = "authwithtoken/" + requireNonNull(tokenHash) + "/" + requireNonNull(user);
        return new EncryptedCommand<>(cmd, Token.class, encryptor);
    }

    public static EncryptedCommand<Token> getToken(final String tokenHash, final String user, final String clientUuid,
                                                   final String clientInfo, final Function<String, String> encryptor) {
        final String cmd = "jdev/sys/gettoken/"
                + requireNonNull(tokenHash) + "/" + requireNonNull(user) + "/2/"
                + requireNonNull(clientUuid) + "/" + requireNonNull(clientInfo);
        return new EncryptedCommand<>(cmd, Token.class, encryptor);
    }

    public static EncryptedCommand<Token> refreshToken(final String tokenHash, final String user,
                                                        final Function<String, String> encryptor) {
        final String cmd = "jdev/sys/refreshtoken/" + requireNonNull(tokenHash) + "/" + requireNonNull(user);
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
