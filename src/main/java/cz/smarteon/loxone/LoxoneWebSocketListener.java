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

    /**
     * Loxone web socket has been closed by remote end.
     * @param code the code returned by the socket when the connection is closed
     */
    default void webSocketRemoteClosed(int code) {}

    /**
     * Loxone web socket has been closed by local end. This is mostly the situation indicating
     * {@link LoxoneWebSocket#close()} has been called.
     * @param code the code returned by the socket when the connection is closed
     */
    default void webSocketLocalClosed(int code) {}
}
