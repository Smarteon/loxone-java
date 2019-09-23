package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;

/**
 * Represents miniserver command
 * @param <T>  type of command response
 */
public class Command<T> {

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
