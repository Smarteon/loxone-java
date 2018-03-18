package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.LoxoneValue;
import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cz.smarteon.loxone.Protocol.HTTP_AUTH_FAIL;
import static cz.smarteon.loxone.Protocol.HTTP_AUTH_TOO_LONG;
import static cz.smarteon.loxone.Protocol.HTTP_NOT_AUTHENTICATED;
import static cz.smarteon.loxone.Protocol.HTTP_NOT_FOUND;
import static cz.smarteon.loxone.Protocol.HTTP_OK;
import static cz.smarteon.loxone.Protocol.HTTP_UNAUTHORIZED;
import static cz.smarteon.loxone.Protocol.isCommandGetToken;
import static cz.smarteon.loxone.Protocol.isCommandGetVisuSalt;
import static cz.smarteon.loxone.Protocol.jsonGetKey;
import static cz.smarteon.loxone.Protocol.jsonGetVisuSalt;
import static cz.smarteon.loxone.Protocol.jsonSecured;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class LoxoneWebSocket {

    private static final Logger log = LoggerFactory.getLogger(LoxoneWebSocket.class);

    private static final String URI_TEMPLATE = "ws://%s/ws/rfc6455";

    private WebSocketClient webSocketClient;
    private final String loxoneAddress;
    final LoxoneAuth loxoneAuth;

    private final List<CommandListener> commandListeners;
    private final List<LoxoneEventListener> eventListeners;

    private ReentrantReadWriteLock connectRwLock = new ReentrantReadWriteLock();
    private CountDownLatch authSeqLatch;
    private CountDownLatch visuLatch;


    public LoxoneWebSocket(String loxoneAddress, LoxoneAuth loxoneAuth) {
        this.loxoneAddress = requireNonNull(loxoneAddress, "loxoneAddress shouldn't be null");
        this.loxoneAuth = requireNonNull(loxoneAuth, "loxoneAuth shouldn't be null");

        this.commandListeners = new LinkedList<>();
        this.eventListeners = new LinkedList<>();

        registerListener(loxoneAuth);
    }

    public void registerListener(final CommandListener listener) {
        commandListeners.add(listener);
    }

    public void registerListener(final LoxoneEventListener listener) {
        eventListeners.add(listener);
    }

    public void sendCommand(final String command) {
        ensureConnection();

        connectRwLock.readLock().lock();
        try {
            waitForAuth(authSeqLatch);
            sendInternal(command);
        } finally {
            connectRwLock.readLock().unlock();
        }
    }

    public void sendSecureCommand(final String command) {
        ensureConnection();

        connectRwLock.readLock().lock();
        try {
            waitForAuth(authSeqLatch);
            if (visuLatch == null || visuLatch.getCount() == 0) {
                visuLatch = new CountDownLatch(1);
                sendInternal(jsonGetVisuSalt(loxoneAuth.getUser()));
            }
            waitForAuth(visuLatch);

            sendInternal(jsonSecured(command, loxoneAuth.getVisuHash()));

        } finally {
            connectRwLock.readLock().unlock();
        }
    }

    public void close() {
        try {
            if (webSocketClient != null) {
                webSocketClient.closeBlocking();
            }
        } catch (InterruptedException e) {
            throw new LoxoneException("Interrupted while closing websocket", e);
        }
    }

    public LoxoneAuth getLoxoneAuth() {
        return loxoneAuth;
    }

    private void ensureConnection() {
        if (!loxoneAuth.isInitialized()) {
            loxoneAuth.init();
        }
        if (webSocketClient == null || !webSocketClient.isOpen()) {
            if (connectRwLock.writeLock().tryLock()) {
                try {
                    authSeqLatch = new CountDownLatch(1);
                    webSocketClient = new LoxoneWebsocketClient(this, URI.create(format(URI_TEMPLATE, loxoneAddress)));
                    webSocketClient.connect();
                } finally {
                    connectRwLock.writeLock().unlock();
                }
            }
        }
    }

    private void waitForAuth(final CountDownLatch latch) {
        try {
            if (latch.await(1, TimeUnit.SECONDS)) {
                log.trace("Wait for authentication successful");
            } else {
                throw new LoxoneException("Unable to authenticate within timeout");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for authentication sequence completion", e);
        }

    }
    void sendInternal(final String command) {
        log.debug("Sending websocket message: " + command);
        webSocketClient.send(command);
    }

    void processMessage(final LoxoneMessage response) {
        if (processHttpResponseCode(response.getCode())) {
            processCommand(response.getControl(), response.getValue());
        } else {
            log.debug(response.toString());
        }
    }

    void processEvents(final MessageHeader msgHeader, final ByteBuffer bytes) {
        switch (msgHeader.getKind()) {
            case EVENT_VALUE:
                final Collection<ValueEvent> valueEvents = Codec.readValueEvents(bytes);
                log.trace("Incoming " + valueEvents);
                for (ValueEvent event : valueEvents) {
                    for (LoxoneEventListener eventListener : eventListeners) {
                        eventListener.onEvent(event);
                    }
                }
                break;
            case EVENT_TEXT:
                final Collection<TextEvent> textEvents = Codec.readTextEvents(bytes);
                log.trace(("Incoming " + textEvents));
                for (TextEvent event : textEvents) {
                    for (LoxoneEventListener eventListener : eventListeners) {
                        eventListener.onEvent(event);
                    }
                }
                break;
            default:
                log.trace("Incoming binary message " + Codec.bytesToHex(bytes.order(ByteOrder.LITTLE_ENDIAN).array()));
        }
    }

    private boolean processHttpResponseCode(final int code) {
        switch (code) {
            case HTTP_OK:
                log.debug("Message successfully processed.");
                return true;
            case HTTP_AUTH_TOO_LONG:
                log.debug("Not authenticated after connection. Authentication took too long.");
                return false;
            case HTTP_NOT_AUTHENTICATED:
                log.debug("Not authenticated. You must send auth request at the first.");
                return false;
            case HTTP_AUTH_FAIL:
                log.debug("Not authenticated. Bad credentials.");
                return false;
            case HTTP_UNAUTHORIZED:
                log.debug("Not authenticated for secured action.");
                return false;
            case HTTP_NOT_FOUND:
                log.debug("Can't find deviceId.");
                return false;
            default:
                log.debug("Unknown response code: " + code + " for message");
                return false;
        }
    }

    private void processCommand(final String command, final LoxoneValue value) {

        CommandListener.State commandState = CommandListener.State.IGNORED;
        final Iterator<CommandListener> listeners = commandListeners.iterator();
        while (listeners.hasNext() && commandState != CommandListener.State.CONSUMED) {
            commandState = commandState.fold(listeners.next().onCommand(command, value));
        }

        if (commandState == CommandListener.State.IGNORED) {
            log.warn("No command listener registered, ignoring command=" + command);
        }

        if (command != null && command.startsWith(Protocol.C_SYS_ENC)) {
            log.debug("Encrypted message");
        }

        if (jsonGetKey(loxoneAuth.getUser()).equals(command)) {
            loxoneAuth.onCommand(command, value);
            // TODO do not always get new token
            sendInternal(loxoneAuth.encryptCommand(loxoneAuth.getTokenCommand()));
        }

        if (isCommandGetToken(command, loxoneAuth.getUser())) {
            // TODO do not always get new token
            if (authSeqLatch != null) {
                sendInternal(Protocol.C_JSON_INIT_STATUS);
                authSeqLatch.countDown();
            } else {
                throw new IllegalStateException("Authentication not guarded");
            }
        }

        if (isCommandGetVisuSalt(command, loxoneAuth.getUser())) {
            if (visuLatch != null) {
                visuLatch.countDown();
            } else {
                throw new IllegalStateException("Authentication not guarded");
            }
        }


    }
}
