package cz.smarteon.loxone;

import cz.smarteon.loxone.app.Control;
import cz.smarteon.loxone.app.LoxoneApp;
import cz.smarteon.loxone.message.ControlCommand;
import cz.smarteon.loxone.message.JsonValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand;
import static java.util.Objects.requireNonNull;

/**
 * Main entry point of the library. Provides complex access to Loxone API. Needs to be started to work properly
 * and stopped to release all the resources correctly (see {@link #start()} amd {@link #stop()}).
 *
 * After {@link #start()} request the instance of {@link LoxoneApp} and then provides it by {@link #app()}, also
 * allows to listen for newly fetched {@link LoxoneApp} using {@link LoxoneAppListener}, set by
 * {@link #registerLoxoneAppListener(LoxoneAppListener)}.
 *
 * Allows to configure the connection to receive update events using {@link #setEventsEnabled(boolean)}. Use
 * {@link LoxoneWebSocket#registerListener(LoxoneEventListener)} to listen for those events.
 *
 * Provides set of methods to send loxone commands based on {@link Control}. Use {@link LoxoneWebSocket#registerListener(CommandResponseListener)} to listen for command responses.
 */
public class Loxone {

    private static final Logger log = LoggerFactory.getLogger(Loxone.class);

    private final LoxoneHttp loxoneHttp;
    private final LoxoneWebSocket loxoneWebSocket;
    private final LoxoneAuth loxoneAuth;

    private final List<LoxoneAppListener> loxoneAppListeners = new LinkedList<>();

    private CountDownLatch appLatch;
    private LoxoneApp loxoneApp;
    private boolean eventsEnabled = false;

    /**
     * Creates new instance of given endpoint, user, password and visualization password.
     * @param endpoint endpoint, can't be null
     * @param user user name, can't be null
     * @param pass password, can't be null
     * @param visuPass visualization password, can't be null
     */
    public Loxone(final @NotNull LoxoneEndpoint endpoint,
                  final @NotNull String user, final @NotNull String pass, final @NotNull String visuPass) {
        // parameters checked in the constructors below
        this.loxoneHttp = new LoxoneHttp(endpoint);
        this.loxoneAuth = new LoxoneAuth(loxoneHttp, user, pass, visuPass);
        this.loxoneWebSocket = new LoxoneWebSocket(endpoint, loxoneAuth);
        init();
    }

    @TestOnly
    Loxone(final @NotNull LoxoneHttp loxoneHttp, final @NotNull LoxoneWebSocket loxoneWebSocket,
           final @NotNull LoxoneAuth loxoneAuth) {
        this.loxoneHttp = requireNonNull(loxoneHttp);
        this.loxoneWebSocket = requireNonNull(loxoneWebSocket);
        this.loxoneAuth = requireNonNull(loxoneAuth);
        init();
    }

    private void init() {
        loxoneWebSocket.registerListener(new LoxAppResponseListener());
    }

    /**
     * Add given {@link LoxoneAppListener} to the list of listeners.
     * @param listener listener to add, can't be null
     */
    public void registerLoxoneAppListener(final @NotNull LoxoneAppListener listener) {
        loxoneAppListeners.add(requireNonNull(listener, "listener can't be null"));
    }

    /**
     * Start the service -  initiates the connection, authenticates, request the {@link LoxoneApp} and ask for event
     * updates (in case {@link #setEventsEnabled(boolean)} set to true).
     * @throws LoxoneException in case something went wrong
     */
    public void start() {
        appLatch = new CountDownLatch(1);
        loxoneWebSocket.sendCommand(Command.LOX_APP);
        try {
            final int timeout = loxoneWebSocket.getAuthTimeoutSeconds() * loxoneWebSocket.getRetries() + 1;
            if (appLatch.await(timeout, TimeUnit.SECONDS)) {
                log.info("Loxone application fetched");
                if (eventsEnabled) {
                    log.info("Signing to receive events");
                    loxoneWebSocket.sendCommand(Command.ENABLE_STATUS_UPDATE);
                }
            } else {
                log.error("Loxone application wasn't fetched within timeout");
                throw new LoxoneException("Loxone application wasn't fetched within timeout");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for loxone application fetch", e);
            throw new LoxoneException("Interrupted while waiting for loxone application fetch", e);
        }

        // let's listen to next websocket open event ie when websocket was restarted
        if (webSocket().getWebSocketListener() == null) {
            webSocket().setWebSocketListener(this::start);
        }
    }

    /**
     * Provides enclosed instance of {@link LoxoneAuth}.
     * @return loxone auth
     */
    @NotNull
    public LoxoneAuth auth() {
        return loxoneAuth;
    }

    /**
     * Provides enclosed instance of {@link LoxoneHttp}.
     * @return loxone http
     */
    @NotNull
    public LoxoneHttp http() {
        return loxoneHttp;
    }

    /**
     * Provides enclosed instance of {@link LoxoneWebSocket}.
     * @return loxone web socket
     */
    @NotNull
    public LoxoneWebSocket webSocket() {
        return loxoneWebSocket;
    }

    /**
     * Get the fetched {@link LoxoneApp}, may return null of the app is not fetched yet.
     * @return fetched app ro null
     */
    @Nullable
    public LoxoneApp app() {
        return loxoneApp;
    }

    /**
     * Send 'pulse' on given control. Use {@link CommandResponseListener} added to {@link #webSocket()} to process the response.
     * @param control control to send 'pulse' on, can't be null
     */
    public void sendControlPulse(final @NotNull Control control) {
        sendControlCommand(control, "Pulse");
    }

    /**
     * Send 'on' on given control. Use {@link CommandResponseListener} added to {@link #webSocket()} to process the response.
     * @param control control to send 'on' on, can't be null
     */
    public void sendControlOn(final @NotNull Control control) {
        sendControlCommand(control, "On");
    }

    /**
     * Send 'off' on given control. Use {@link CommandResponseListener} added to {@link #webSocket()} to process the response.
     * @param control control to send 'off' on, can't be null
     */
    public void sendControlOff(final @NotNull Control control) {
        sendControlCommand(control, "Off");
    }

    private void sendControlCommand(final Control control, final String command) {
        requireNonNull(control, "control can't be null");
        final ControlCommand<JsonValue> controlCommand = genericControlCommand(control.getUuid().toString(), command);
        if (control.isSecured()) {
            webSocket().sendSecureCommand(controlCommand);
        } else {
            webSocket().sendCommand(controlCommand);
        }
    }

    /**
     * Stops the service closing underlying resources, namely {@link LoxoneWebSocket}.
     */
    public void stop() {
        loxoneWebSocket.close();
    }

    /**
     * Whether the update events are enabled and requested from miniserver. By default set to false.
     * @return true when update events enabled, false otherwise
     */
    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    /**
     * Whether the update events are enabled and requested from miniserver. Changing the value only has effect
     * before calling {@link #start()}.
     * @param eventsEnabled true for events enabled, false by default.
     */
    public void setEventsEnabled(final boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
    }

    private class LoxAppResponseListener implements CommandResponseListener<LoxoneApp> {

        @Override
        public @NotNull State onCommand(final @NotNull Command<? extends LoxoneApp> command, final @NotNull LoxoneApp message) {
            loxoneApp = command.ensureResponse(message);
            loxoneAppListeners.forEach(listener -> listener.onLoxoneApp(loxoneApp));
            if (appLatch != null) {
                appLatch.countDown();
            }
            return State.READ;
        }

        @Override
        public boolean accepts(final @NotNull Class clazz) {
            return LoxoneApp.class.equals(clazz);
        }
    }
}
