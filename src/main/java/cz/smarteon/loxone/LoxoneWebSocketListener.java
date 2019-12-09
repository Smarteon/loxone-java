package cz.smarteon.loxone;

/**
 * Allows to listen for loxone web socket events.
 * @see LoxoneWebSocket#setWebSocketListener(LoxoneWebSocketListener)
 */
public interface LoxoneWebSocketListener {
    /**
     * Loxone web socket has been opened.
     */
    void webSocketOpened();
}
