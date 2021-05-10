package cz.smarteon.loxone;

/**
 * Allows to be notified about authentication events. See {@link LoxoneAuth} for more info.
 */
public interface AuthListener {

    /**
     * Event triggered, just before the authentication mechanisms is started.
     */
    void beforeAuth();

    /**
     * Event triggered when authentication is completed and underlying websocket connection can be used send authorized
     * commands.
     */
    void authCompleted();

    /**
     * Event triggered, just before the visualization authentication mechanisms is started.
     */
    void beforeVisuAuth();

    /**
     * Event triggered when visualization authentication is completed and {@link LoxoneAuth} is ready to send secured command.
     */
    void visuAuthCompleted();
}
