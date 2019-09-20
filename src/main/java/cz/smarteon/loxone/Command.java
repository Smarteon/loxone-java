package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.system.status.MiniserverStatus;

/**
 * Represents miniserver command
 * @param <T>  type of command response
 */
public class Command<T> {

    /**
     * Miniserver status API.
     */
    public static final Command<MiniserverStatus> DATA_STATUS = xmlHttpCommand("data/status", MiniserverStatus.class);

    /**
     * Basic information about API. Unauthenticated.
     */
    public static final Command<LoxoneMessage> DEV_CFG_API = loxoneMessageCommand("jdev/cfg/api");

    /**
     * API's public key.
     */
    public static final Command<LoxoneMessage> DEV_SYS_GETPUBLICKEY = loxoneMessageCommand("jdev/sys/getPublicKey");

    private final String command;
    private final Type type;
    private final Class<T> responseType;
    private final boolean httpSupported;
    private final boolean wsSupported;

    private Command(final String command, final Type type, final Class<T> responseType, final boolean httpSupported,
                    final boolean wsSupported) {
        this.command = command;
        this.type = type;
        this.responseType = responseType;
        this.httpSupported = httpSupported;
        this.wsSupported = wsSupported;
    }

    private static Command<LoxoneMessage> loxoneMessageCommand(final String command) {
        return new Command<>(command, Type.JSON, LoxoneMessage.class, true, false);
    }

    private static <T> Command<T> xmlHttpCommand(final String command, final Class<T> responseType) {
        return new Command<>(command, Type.XML, responseType, true, false);
    }

    /**
     * String representation of the command.
     * @return command string
     */
    public String getCommand() {
        return command;
    }

    /**
     * Command type
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Command response type
     * @return response type
     */
    public Class<T> getResponseType() {
        return responseType;
    }

    /**
     * Whether the command can be used through HTTP API
     * @return true when command supported by HTTP API, false otherwise
     */
    public boolean isHttpSupported() {
        return httpSupported;
    }

    /**
     * Whether the command can be used through Web Socket API
     * @return true when command supported by Web Socket API, false otherwise
     */
    public boolean isWsSupported() {
        return wsSupported;
    }

    /**
     * Command content type.
     */
    public enum Type {
        JSON, XML
    }
}
