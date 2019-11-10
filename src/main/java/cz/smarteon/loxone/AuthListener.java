package cz.smarteon.loxone;

/**
 * Allows to be notified about authentication events. See {@link LoxoneAuth} for more info.
 */
public interface AuthListener {

    /**
     * Event triggered when authentication is completed and underlying websocket connection can be used send authorized
     * commands.
     */
    void authCompleted();

    /**
     * Event triggered when visualization authentication is completed and {@link LoxoneAuth} is ready to send secured command.
     */
    void visuAuthCompleted();
}
