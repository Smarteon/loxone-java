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
}
