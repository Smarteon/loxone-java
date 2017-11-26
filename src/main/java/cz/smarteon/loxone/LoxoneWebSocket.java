package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
import static cz.smarteon.loxone.Protocol.jsonKeyExchange;
import static cz.smarteon.loxone.Protocol.jsonSecured;
import static java.lang.String.format;

public class LoxoneWebSocket {

    private static final Logger log = LoggerFactory.getLogger(LoxoneWebSocket.class);

    private static final String URI_TEMPLATE = "ws://%s/ws/rfc6455";

    private final WebSocketClient webSocketClient;
    private final LoxoneAuth loxoneAuth;

    private final List<CommandListener> commandListeners;

    private ReentrantReadWriteLock connectRwLock = new ReentrantReadWriteLock();
    private CountDownLatch authSeqLatch;
    private CountDownLatch visuLatch;


    public LoxoneWebSocket(String loxoneAddress, LoxoneAuth loxoneAuth) {
        webSocketClient = new LoxoneWebsocketClient(URI.create(format(URI_TEMPLATE, loxoneAddress)));
        this.loxoneAuth = loxoneAuth;

        this.commandListeners = new LinkedList<>();

        registerListener(loxoneAuth);
    }

    public void registerListener(CommandListener listener) {
        commandListeners.add(listener);
    }

    public void sendCommand(String command) {
        ensureConnection();

        connectRwLock.readLock().lock();
        try {
            authSeqLatch.await();
            sendInternal(command);
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for authentication sequence completion", e);
        } finally {
            connectRwLock.readLock().unlock();
        }
    }

    public void sendSecureCommand(String command) {
        ensureConnection();

        connectRwLock.readLock().lock();
        try {
            authSeqLatch.await();

            if (visuLatch == null || visuLatch.getCount() == 0) {
                visuLatch = new CountDownLatch(1);
                sendInternal(jsonGetVisuSalt(loxoneAuth.getUser()));
            }
            visuLatch.await();

            sendInternal(jsonSecured(command, loxoneAuth.getVisuHash()));
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for authentication sequence completion", e);
        } finally {
            connectRwLock.readLock().unlock();
        }
    }

    public void close() {
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            throw new LoxoneException("Interrupted while closing websocket", e);
        }
    }

    void ensureConnection() {
        if (!loxoneAuth.isInitialized()) {
            loxoneAuth.init();
        }
        if (!webSocketClient.isOpen()) {
            if (connectRwLock.writeLock().tryLock()) {
                try {
                    authSeqLatch = new CountDownLatch(1);
                    webSocketClient.connect();
                } finally {
                    connectRwLock.writeLock().unlock();
                }
            }
        }
    }

    void sendInternal(String command) {
        log.debug("Sending websocket message: " + command);
        webSocketClient.send(command);
    }

    private void parseAndProcessResponse(LoxoneMessage response) {
        if (processHttpResponseCode(response.getCode())) {
            processCommand(response.getControl(), response.getValue());
        } else {
            log.debug(response.toString());
        }
    }

    private boolean processHttpResponseCode(int code) {
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

    private void processCommand(String command, Object value) {

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

    private class LoxoneWebsocketClient extends WebSocketClient {

        LoxoneWebsocketClient(URI uri) {
            super(uri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            log.info("Opened");
            sendInternal(jsonKeyExchange(loxoneAuth.getSessionKey()));
            sendInternal(jsonGetKey(loxoneAuth.getUser()));
        }

        @Override
        public void onMessage(String message) {
            log.trace("Incoming message " + message);
            try {
                parseAndProcessResponse(Codec.readMessage(message));
            } catch (IOException e) {
                log.error("Can't parse response: " + e.getMessage());
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.info("Closed " + reason);
        }

        @Override
        public void onError(Exception ex) {
            log.info("Error " + ex.getMessage() + ex.getClass());
        }
    }
}
