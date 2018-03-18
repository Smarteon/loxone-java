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
import java.util.concurrent.atomic.AtomicReference;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Protocol.jsonGetKey;
import static cz.smarteon.loxone.Protocol.jsonKeyExchange;

class LoxoneWebsocketClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(LoxoneWebsocketClient.class);

    private LoxoneWebSocket ws;

    private AtomicReference<MessageHeader> msgHeaderRef = new AtomicReference<>();

    LoxoneWebsocketClient(LoxoneWebSocket ws, URI uri) {
        super(uri);
        this.ws = ws;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Opened");
        ws.sendInternal(jsonKeyExchange(ws.loxoneAuth.getSessionKey()));
        ws.sendInternal(jsonGetKey(ws.loxoneAuth.getUser()));
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
            if (msgHeaderRef.get() == null && msgHeaderRef.compareAndSet(null, Codec.readHeader(bytes))) {
                log.trace("Incoming message header " + msgHeaderRef.get());
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
    }

    @Override
    public void onError(Exception ex) {
        log.info("Error of loxone connection", ex);
    }
}
