package cz.smarteon.loxone;

/**
 * Allows to listen on messages representing {@link Command}s' responses.
 * @param <T> type of response message
 */
interface CommandRequestResponseListener<T> extends CommandResponseListener<T>{

    void registerWebSocket(LoxoneWebSocket loxoneWebSocket);
}
