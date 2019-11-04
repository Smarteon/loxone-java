package cz.smarteon.loxone;

import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.MessageKind;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Command.KEEP_ALIVE;
import static java.util.Objects.requireNonNull;

/**
 * {@link WebSocketClient} providing:
 * <ul>
 *     <li>Initial connection setup</li>
 *     <li>Keepalive mechanism</li>
 *     <li>Loxone protocol guard (parses header messages)</li>
 * </ul>
 */
class LoxoneWebsocketClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(LoxoneWebsocketClient.class);

    private static final int KEEP_ALIVE_INTERVAL_MINUTES = 4;
    private static final int KEEP_ALIVE_RESPONSE_TIMEOUT_SECONDS = 30;

    private LoxoneWebSocket ws;

    private AtomicReference<MessageHeader> msgHeaderRef = new AtomicReference<>();

    private ScheduledExecutorService keepAliveScheduler = Executors.newSingleThreadScheduledExecutor();
    private Runnable keepAliveTask;
    private CountDownLatch keepAliveLatch;
    private ScheduledFuture keepAliveFuture;

    /**
     * Creats new instance
     * @param ws callback for processing messages and events
     * @param uri websocket URI to connect to
     */
    LoxoneWebsocketClient(final LoxoneWebSocket ws, final URI uri) {
        super(uri);
        this.ws = requireNonNull(ws);
        this.keepAliveTask = () -> {
            LoxoneWebsocketClient.this.ws.sendInternal(KEEP_ALIVE);
            keepAliveLatch = new CountDownLatch(1);
            try {
                if (!keepAliveLatch.await(KEEP_ALIVE_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    log.info("Keepalive response not received within timeout, closing connection");
                    LoxoneWebsocketClient.this.ws.close();
                }
            } catch (InterruptedException e) {
                log.debug("Keepalive latch has been interrupted");
            }
        };
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        log.info("Opened");

        ws.loxoneAuth.startAuthentication();

        // schedule the keep alive guard
        keepAliveFuture = keepAliveScheduler.scheduleAtFixedRate(keepAliveTask,
                KEEP_ALIVE_INTERVAL_MINUTES, KEEP_ALIVE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Processes text message. The previous message header should have been of kind {@link MessageKind#TEXT}
     * @param message message.
     */
    @Override
    public void onMessage(final String message) {
        log.trace("Incoming message " + message);
        final MessageHeader msgHeader = msgHeaderRef.getAndSet(null);
        if (msgHeader != null && msgHeader.getKind() != MessageKind.TEXT) {
            log.warn("Got text message but " + msgHeader.getKind() + " has been expected");
        }
        try {
            ws.processMessage(Codec.readMessage(message));
        } catch (IOException e) {
            log.error("Can't parse response: " + e.getMessage());
        }
    }

    /**
     * Processes binary message. That can be one of:
     * <ul>
     *     <li>{@link MessageHeader#KEEP_ALIVE} - used to guard the connection</li>
     *     <li>Regular {@link MessageHeader} - set for next message parsing</li>
     *     <li>Binary message of events - previous header is used to parse and process</li>
     * </ul>
     * @param bytes message
     */
    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            if (msgHeaderRef.get() == null) {
                final MessageHeader header = Codec.readHeader(bytes);
                if (MessageHeader.KEEP_ALIVE.equals(header)) {
                    log.trace("Incoming keepalive");
                    keepAliveLatch.countDown();
                } else if (msgHeaderRef.compareAndSet(null, header)) {
                    log.trace("Incoming message header " + msgHeaderRef.get());
                } else {
                    bytes.rewind();
                    ws.processEvents(msgHeaderRef.getAndSet(null), bytes);
                }
            } else {
                ws.processEvents(msgHeaderRef.getAndSet(null), bytes);
            }
        } catch (Throwable t) {
            bytes.rewind();
            log.error("Can't read binary message " + bytesToHex(bytes.array()), t);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed " + reason);
        if (keepAliveFuture != null) {
            keepAliveFuture.cancel(true);
        }
    }

    @Override
    public void onError(Exception ex) {
        log.info("Error of loxone connection", ex);
    }
}
