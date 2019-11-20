package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ControlCommand;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cz.smarteon.loxone.Command.ENABLE_STATUS_UPDATE;
import static cz.smarteon.loxone.Command.KEEP_ALIVE;
import static cz.smarteon.loxone.Protocol.HTTP_AUTH_FAIL;
import static cz.smarteon.loxone.Protocol.HTTP_AUTH_TOO_LONG;
import static cz.smarteon.loxone.Protocol.HTTP_NOT_AUTHENTICATED;
import static cz.smarteon.loxone.Protocol.HTTP_NOT_FOUND;
import static cz.smarteon.loxone.Protocol.HTTP_OK;
import static cz.smarteon.loxone.Protocol.HTTP_UNAUTHORIZED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class LoxoneWebSocket {

    private static final Logger log = LoggerFactory.getLogger(LoxoneWebSocket.class);

    private static final String URI_TEMPLATE = "ws://%s/ws/rfc6455";

    private WebSocketClient webSocketClient;
    private final String loxoneAddress;
    final LoxoneAuth loxoneAuth;

    private final List<CommandResponseListener> commandResponseListeners;
    private final List<LoxoneEventListener> eventListeners;
    private final Queue<Command<?>> commands;

    private ReentrantReadWriteLock connectRwLock = new ReentrantReadWriteLock();
    private CountDownLatch authSeqLatch;
    private CountDownLatch visuLatch;

    private int authTimeoutSeconds = 1;
    private int visuTimeoutSeconds = 3;
    private int retries = 5;

    public LoxoneWebSocket(@NotNull final String loxoneAddress, @NotNull final LoxoneAuth loxoneAuth) {
        this.loxoneAddress = requireNonNull(loxoneAddress, "loxoneAddress shouldn't be null");
        this.loxoneAuth = requireNonNull(loxoneAuth, "loxoneAuth shouldn't be null");

        this.commandResponseListeners = new LinkedList<>();
        this.eventListeners = new LinkedList<>();
        this.commands = new ConcurrentLinkedQueue<>();

        // link loxoneAuth as command listener
        registerListener(loxoneAuth);

        // register auth guard as auth listener
        loxoneAuth.registerAuthListener(new LoxoneAuthListener());

        // allow auth to send commands
        loxoneAuth.setCommandSender(this::sendInternal);
    }

    public void registerListener(@NotNull final CommandResponseListener listener) {
        commandResponseListeners.add(listener);
    }

    public void registerListener(@NotNull final LoxoneEventListener listener) {
        eventListeners.add(listener);
    }

    public void sendCommand(@NotNull final Command command) {
        requireNonNull(command, "command can't be null");
        if (command.isWsSupported()) {
            sendWithRetry(command, retries);
        } else {
            throw new IllegalArgumentException("Only websocket commands are supported");
        }
    }

    public void sendSecureCommand(@NotNull final ControlCommand command) {
        sendSecureWithRetry(command, retries);
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

    @NotNull
    public LoxoneAuth getLoxoneAuth() {
        return loxoneAuth;
    }

    /**
     * Set the seconds, it waits for successful authentication, until give up.
     *
     * @param authTimeoutSeconds authentication timeout in seconds
     */
    public void setAuthTimeoutSeconds(final int authTimeoutSeconds) {
        this.authTimeoutSeconds = authTimeoutSeconds;
    }

    /**
     * Set the seconds, it waits for successful visual authentication, until give up.
     *
     * @param visuTimeoutSeconds visual authentication timeout in seconds
     */
    public void setVisuTimeoutSeconds(final int visuTimeoutSeconds) {
        this.visuTimeoutSeconds = visuTimeoutSeconds;
    }

    /**
     * Set the number of retries for successful authentication, until give up.
     *
     * @param retries number of retries
     */
    public void setRetries(final int retries) {
        this.retries = retries;
    }

    private void ensureConnection() {
        if (!loxoneAuth.isInitialized()) {
            loxoneAuth.init();
        }
        if (webSocketClient == null || !webSocketClient.isOpen()) {
            log.trace("(Re)opening websocket connection");
            if (connectRwLock.writeLock().tryLock()) {
                try {
                    authSeqLatch = new CountDownLatch(1);
                    webSocketClient = new LoxoneWebsocketClient(this, URI.create(format(URI_TEMPLATE, loxoneAddress)));
                    webSocketClient.connect();
                } finally {
                    connectRwLock.writeLock().unlock();
                }
            }
        } else if (!loxoneAuth.isUsable()) {
            log.info("Authentication is not usable => starting the authentication");
            authSeqLatch = new CountDownLatch(1);
            loxoneAuth.startAuthentication();
        }
    }

    private void waitForAuth(final CountDownLatch latch, final int timeout, final boolean close) {
        try {
            if (latch.await(timeout, TimeUnit.SECONDS)) {
                log.trace("Waiting for authentication has been successful");
            } else {
                if (close) {
                    close();
                }
                throw new LoxoneConnectionException("Unable to authenticate within timeout");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for authentication sequence completion", e);
        }
    }

    private void sendWithRetry(final Command command, final int retries) {
        ensureConnection();

        try {
            connectRwLock.readLock().lock();
            try {
                waitForAuth(authSeqLatch, authTimeoutSeconds, true);
                sendInternal(command);
            } finally {
                connectRwLock.readLock().unlock();
            }
        } catch (LoxoneConnectionException e) {
            if (retries > 0) {
                log.info("Connection or authentication failed, retrying...");
                waitForRetry();
                sendWithRetry(command, retries - 1);
            } else {
                log.info("Connection or authentication failed too many times, give up");
                throw new LoxoneException("Unable to authenticate within timeout with retry", e);
            }
        }
    }

    private void sendSecureWithRetry(final ControlCommand<?> command, final int retries) {
        ensureConnection();

        try {
            connectRwLock.readLock().lock();
            try {
                waitForAuth(authSeqLatch, authTimeoutSeconds, true);
                if (visuLatch == null || visuLatch.getCount() == 0) {
                    visuLatch = new CountDownLatch(1);
                    loxoneAuth.startVisuAuthentication();
                }
                waitForAuth(visuLatch, visuTimeoutSeconds, false);

                sendInternal(new SecuredCommand<>(command, loxoneAuth.getVisuHash()));
            } finally {
                connectRwLock.readLock().unlock();
            }
        } catch (LoxoneConnectionException e) {
            if (retries > 0) {
                log.info("Connection or authentication failed, retrying...");
                waitForRetry();
                sendSecureWithRetry(command, retries - 1);
            } else {
                log.info("Connection or authentication failed too many times, give up");
                throw new LoxoneException("Unable to authenticate within timeout with retry", e);
            }
        }
    }

    private void waitForRetry() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
        }
    }


    void sendInternal(final Command command) {
        log.debug("Sending websocket message: " + command.getCommand());
        webSocketClient.send(command.getCommand());
        // KEEP_ALIVE command has no response at all
        if (! KEEP_ALIVE.getCommand().equals(command.getCommand())) {
            commands.add(command);
        }
    }

    void processMessage(final String message) {
        try {
            final Command<?> command = commands.remove();
            if (! Void.class.equals(command.getResponseType())) {
                final Object parsedMessage = Codec.readMessage(message, command.getResponseType());
                if (parsedMessage instanceof LoxoneMessage) {
                    final LoxoneMessage loxoneMessage = (LoxoneMessage) parsedMessage;
                    if (checkLoxoneMessage(command, loxoneMessage)) {
                        processCommand(command, loxoneMessage);
                    } else {
                        log.debug(loxoneMessage.toString());
                    }
                } else {
                    processCommand(command, command.ensureResponse(parsedMessage));
                }
            }
        } catch (NoSuchElementException e) {
            log.error("No command expected!", e);
        } catch (IOException e) {
            log.error("Can't parse response: " + e.getMessage());
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

    private boolean checkLoxoneMessage(final Command command, final LoxoneMessage loxoneMessage) {
        switch (loxoneMessage.getCode()) {
            case HTTP_OK:
                log.debug("Message successfully processed.");
                if (command.is(loxoneMessage.getControl())) {
                    return true;
                } else {
                    log.error("Unexpected message with control " + loxoneMessage.getControl());
                    return false;
                }
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
                log.debug("Unknown response code: " + loxoneMessage.getCode() + " for message");
                return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void processCommand(final Command<?> command, final Object message) {
        CommandResponseListener.State commandState = CommandResponseListener.State.IGNORED;
        final Iterator<CommandResponseListener> listeners = commandResponseListeners.iterator();
        while (listeners.hasNext() && commandState != CommandResponseListener.State.CONSUMED) {
            final CommandResponseListener next = listeners.next();
            if (next.accepts(message.getClass())) {
                commandState = commandState.fold(next.onCommand(command, message));
            }
        }

        if (commandState == CommandResponseListener.State.IGNORED) {
            log.warn("No command listener registered, ignoring command=" + command);
        }

        if (command != null && command.getCommand().startsWith(Protocol.C_SYS_ENC)) {
            log.debug("Encrypted message");
        }
    }

    private class LoxoneAuthListener implements AuthListener {

        @Override
        public void authCompleted() {
            log.info("Authentication completed");
            if (authSeqLatch != null) {
                sendInternal(ENABLE_STATUS_UPDATE);
                authSeqLatch.countDown();
            } else {
                throw new IllegalStateException("Authentication not guarded");
            }
        }

        @Override
        public void visuAuthCompleted() {
            log.info("Visualization authentication completed");
            if (visuLatch != null) {
                visuLatch.countDown();
            } else {
                throw new IllegalStateException("Visualization authentication not guarded");
            }
        }
    }
}
