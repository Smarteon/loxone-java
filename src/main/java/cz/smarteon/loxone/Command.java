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

    private final String command;
    private final Type type;
    private final Class<T> responseType;
    private final boolean httpSupported;
    private final boolean wsSupported;

    private final String toMatch;

    protected Command(final String command, final Type type, final Class<T> responseType, final boolean httpSupported,
                    final boolean wsSupported) {
        this.command = command;
        this.type = type;
        this.responseType = responseType;
        this.httpSupported = httpSupported;
        this.wsSupported = wsSupported;

        if (Type.JSON.equals(type)) {
            this.toMatch = command.substring(1);
        } else {
            this.toMatch = command;
        }
    }

    private static <T> Command<T> xmlHttpCommand(final String command, final Class<T> responseType) {
        return new Command<>(command, Type.XML, responseType, true, false);
    }

    /**
     * Check whether this command matches the given argument.
     * @param toCompare command to compare
     * @return true when the argument contains this command identifier, false otherwise
     */
    public boolean is(final String toCompare) {
        return toCompare != null && toCompare.contains(this.toMatch);
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
