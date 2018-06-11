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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Protocol.jsonGetKey;
import static cz.smarteon.loxone.Protocol.jsonKeyExchange;

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

    LoxoneWebsocketClient(LoxoneWebSocket ws, URI uri) {
        super(uri);
        this.ws = ws;
        this.keepAliveTask = new Runnable() {
            @Override
            public void run() {
                LoxoneWebsocketClient.this.ws.sendInternal("keepalive");
                keepAliveLatch = new CountDownLatch(1);
                try {
                    if (!keepAliveLatch.await(KEEP_ALIVE_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        log.info("Keepalive response not received within timeout, closing connection");
                        LoxoneWebsocketClient.this.ws.close();
                    }
                } catch (InterruptedException e) {
                    log.debug("Keepalive latch has been interrupted");
                }
            }
        };
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Opened");
        ws.sendInternal(jsonKeyExchange(ws.loxoneAuth.getSessionKey()));
        ws.sendInternal(jsonGetKey(ws.loxoneAuth.getUser()));

        keepAliveFuture = keepAliveScheduler.scheduleAtFixedRate(keepAliveTask,
                KEEP_ALIVE_INTERVAL_MINUTES, KEEP_ALIVE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void onMessage(String message) {
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
