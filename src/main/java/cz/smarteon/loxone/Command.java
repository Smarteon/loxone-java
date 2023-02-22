package cz.smarteon.loxone;

import cz.smarteon.loxone.app.LoxoneApp;
import cz.smarteon.loxone.app.MiniserverType;
import cz.smarteon.loxone.system.status.MiniserverStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents miniserver command.
 * @param <T>  type of command response
 */
public class Command<T> {

    /**
     * Miniserver Application.
     */
    public static final Command<LoxoneApp> LOX_APP = new Command<>("data/LoxAPP3.json", Type.JSON,
            LoxoneApp.class, true, true, MiniserverType.KNOWN);

    /**
     * Miniserver status API.
     */
    public static final Command<MiniserverStatus> DATA_STATUS = new Command<>("data/status", Type.XML,
            MiniserverStatus.class, true, false, MiniserverType.ALL);

    /**
     * Allow keep-alive WS guard.
     */
    public static final Command<Void> KEEP_ALIVE = voidWsCommand(MiniserverType.KNOWN, "keepalive");

    /**
     * Enable sending miniserver status updates through WS.
     */
    public static final Command<Void> ENABLE_STATUS_UPDATE =
            voidWsCommand(MiniserverType.KNOWN, "jdev/sps/enablebinstatusupdate");

    private final String command;
    private final Type type;
    private final Class<T> responseType;
    private final boolean httpSupported;
    private final boolean wsSupported;
    private final MiniserverType[] supportedMiniservers;

    private final String shouldContain;

    protected Command(final String command, final Type type, final Class<T> responseType, final boolean httpSupported,
                      final boolean wsSupported, final @NotNull MiniserverType[] supportedMiniservers) {
        this.command = command;
        this.type = type;
        this.responseType = responseType;
        this.httpSupported = httpSupported;
        this.wsSupported = wsSupported;
        this.supportedMiniservers = requireNonNull(supportedMiniservers, "supportedMiniservers can't be null");

        if (Type.JSON.equals(type)) {
            this.shouldContain = command.substring(1);
        } else {
            this.shouldContain = command;
        }
    }

    /**
     * The response is expected to come, however it's empty or we don't want to process it.
     *
     * @param supportedMiniservers miniservers supported by the command
     * @param template command template
     * @param params command params
     * @return void websocket command
     */
    static Command<Void> voidWsCommand(final MiniserverType[] supportedMiniservers, final String template,
                                       final String... params) {
        return new Command<>(String.format(requireNonNull(template), (Object[]) params), null, Void.class, false, true,
                supportedMiniservers);
    }

    /**
     * Session key exchange command.
     * @param sessionKey key to exchange
     * @return new key exchange command
     */
    static Command<Void> keyExchange(final String sessionKey) {
        return voidWsCommand(MiniserverType.KNOWN, "jdev/sys/keyexchange/%s", sessionKey);
    }

    /**
     * Kill token command.
     * @param tokenHash hashed token
     * @param user token uses
     * @return void websocket command
     */
    static Command<Void> killToken(final String tokenHash, final String user) {
        return voidWsCommand(MiniserverType.KNOWN, "jdev/sys/killtoken/%s/%s", tokenHash, user);
    }

    /**
     * Check whether this command matches the given argument.
     * @param toCompare command to compare
     * @return true when the argument contains this command identifier, false otherwise
     */
    public boolean is(final String toCompare) {
        return toCompare != null && toCompare.contains(this.shouldContain);
    }

    /**
     * Check whether this command is supported by given miniserver type.
     * @param miniserver miniserver to check the compatibility
     * @return true when this command is supported by given miniserver, false otherwise
     */
    public boolean supportsMiniserver(final @Nullable MiniserverType miniserver) {
        return Arrays.asList(supportedMiniservers).contains(miniserver);
    }

    /**
     * String representation of the command.
     * @return command string
     */
    public String getCommand() {
        return command;
    }

    /**
     * Command type.
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Command response type.
     * @return response type
     */
    public Class<T> getResponseType() {
        return responseType;
    }

    /**
     * Whether the command can be used through HTTP API.
     * @return true when command supported by HTTP API, false otherwise
     */
    public boolean isHttpSupported() {
        return httpSupported;
    }

    /**
     * Whether the command can be used through Web Socket API.
     * @return true when command supported by Web Socket API, false otherwise
     */
    public boolean isWsSupported() {
        return wsSupported;
    }

    /**
     * String which should be contained in the argument passed to {@link #is(String)} to return true.
     * @return string should be contained when comparing this command
     */
    public String getShouldContain() {
        return shouldContain;
    }

    /**
     * Miniserver types supporting this command.
     * @return miniservers which support this command
     */
    @NotNull
    public MiniserverType[] getSupportedMiniservers() {
        return supportedMiniservers;
    }

    /**
     * Ensures the given response being the type of this command's compatible response.
     * Returns the ensured response or throws the exception.
     *
     * @param response response to ensure
     * @return ensured response
     */
    @SuppressWarnings("unchecked")
    @NotNull
    T ensureResponse(@Nullable final Object response) {
        if (response != null && responseType.isAssignableFrom(response.getClass())) {
            return (T) response;
        } else {
            throw new LoxoneException("Expected type of response " + responseType);
        }
    }

    /**
     * Command content type.
     */
    public enum Type {
        JSON, XML, FILE
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Command<?> command1 = (Command<?>) o;
        return httpSupported == command1.httpSupported
                && wsSupported == command1.wsSupported
                && Objects.equals(command, command1.command)
                && type == command1.type
                && Objects.equals(responseType, command1.responseType)
                && Objects.equals(shouldContain, command1.shouldContain)
                && Arrays.equals(supportedMiniservers, command1.supportedMiniservers);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] {
                        command, type, responseType, httpSupported, wsSupported, shouldContain, supportedMiniservers });
    }
}
