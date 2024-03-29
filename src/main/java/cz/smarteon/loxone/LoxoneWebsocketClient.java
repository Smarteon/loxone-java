package cz.smarteon.loxone;

import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.MessageKind;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Command.KEEP_ALIVE;
import static java.util.Objects.requireNonNull;

/**
 * {@link WebSocketClient} providing:
 * <ul>
 *     <li>Initial connection setup</li>
 *     <li>Keepalive mechanism</li>
 *     <li>Loxone protocol guard (parses header messages).</li>
 * </ul>
 */
class LoxoneWebsocketClient extends WebSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(LoxoneWebsocketClient.class);

    private static final int KEEP_ALIVE_INTERVAL_MINUTES = 4;
    private static final int KEEP_ALIVE_RESPONSE_TIMEOUT_SECONDS = 30;

    private final LoxoneWebSocket ws;

    private final AtomicReference<MessageHeader> msgHeaderRef = new AtomicReference<>();

    private final Runnable keepAliveTask;
    private CountDownLatch keepAliveLatch;
    private ScheduledFuture keepAliveFuture;

    private AtomicBoolean onClosedCalled = new AtomicBoolean(false);

    /**
     * Creates new instance.
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
                    LOG.info("Keepalive response not received within timeout, closing connection");
                    LoxoneWebsocketClient.this.ws.closeWebSocket();
                }
            } catch (InterruptedException e) {
                LOG.debug("Keepalive latch has been interrupted");
            }
        };
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        LOG.info("Opened");

        ws.connectionOpened();

        // schedule the keep alive guard
        keepAliveFuture = ws.getScheduler().scheduleAtFixedRate(keepAliveTask,
                KEEP_ALIVE_INTERVAL_MINUTES, KEEP_ALIVE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Processes text message. The previous message header should have been of kind {@link MessageKind#TEXT}
     * @param message message.
     */
    @Override
    public void onMessage(final String message) {
        LOG.trace("Incoming message " + message);
        final MessageHeader msgHeader = msgHeaderRef.getAndSet(null);
        if (msgHeader != null && msgHeader.getKind() != MessageKind.TEXT) {
            LOG.warn("Got text message but " + msgHeader.getKind() + " has been expected");
        }
        ws.processMessage(message);
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
                    LOG.trace("Incoming keepalive");
                    keepAliveLatch.countDown();
                } else if (msgHeaderRef.compareAndSet(null, header)) {
                    LOG.trace("Incoming message header " + msgHeaderRef.get());
                } else {
                    bytes.rewind();
                    ws.processEvents(msgHeaderRef.getAndSet(null), bytes);
                }
            } else {
                ws.processEvents(msgHeaderRef.getAndSet(null), bytes);
            }
        } catch (Throwable t) {
            bytes.rewind();
            LOG.error("Can't read binary message " + bytesToHex(bytes.array()), t);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (!onClosedCalled.getAndSet(true)) {
            LOG.info("Closed by " + (remote ? "remote" : "local") + " end because of " + code + ": " + reason);
            ws.wsClosed();
            if (keepAliveFuture != null) {
                keepAliveFuture.cancel(true);
            }
            ws.connectionClosed(code, remote);
            if (remote && code != CloseFrame.NEVER_CONNECTED) {
                ws.autoRestart();
            }
        }
    }

    @Override
    public void onError(Exception ex) {
        LOG.info("Error of loxone connection", ex);
    }
}
