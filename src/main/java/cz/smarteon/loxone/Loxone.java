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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
    private final LoxoneWebSocketListener webSocketListener = this::start;
    private final Map<LoxoneEndpoint, LoxoneHttp> clientMiniserversHttp = new HashMap<>();

    private CountDownLatch appLatch;
    private LoxoneApp loxoneApp;
    private boolean eventsEnabled = false;

    /**
     * Creates new instance of given endpoint, user and password.
     * @param endpoint endpoint, can't be null
     * @param user user name, can't be null
     * @param pass password, can't be null
     */
    public Loxone(final @NotNull LoxoneEndpoint endpoint, final @NotNull String user, final @NotNull String pass) {
       this(endpoint, user, pass, null);
    }

    /**
     * Creates new instance of given endpoint, user, password and visualization password.
     * @param endpoint endpoint, can't be null
     * @param user user name, can't be null
     * @param pass password, can't be null
     * @param visuPass visualization password, can be null
     */
    public Loxone(final @NotNull LoxoneEndpoint endpoint,
                  final @NotNull String user, final @NotNull String pass, final @Nullable String visuPass) {
        this(new LoxoneProfile(endpoint, user, pass, visuPass));
    }

    /**
     * Creates new instance of given profile.
     * @param profile profile, can't be null
     */
    public Loxone(final @NotNull LoxoneProfile profile) {
        requireNonNull(profile, "profile shouldn't be null");
        this.loxoneHttp = new LoxoneHttp(profile.getEndpoint());
        this.loxoneAuth = new LoxoneAuth(loxoneHttp, profile);
        this.loxoneWebSocket = new LoxoneWebSocket(profile.getEndpoint(), loxoneAuth);
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
     * The method is blocking - waiting for {@link LoxoneApp} first fetch for timeout derived from underlying
     * {@link LoxoneWebSocket}. It means that after successful completion of this method {@link #app()} should return
     * fetched {@link LoxoneApp}.
     * @throws LoxoneException in case something went wrong - the app request couldn't be send or the fetch took too long.
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
        webSocket().registerWebSocketListener(webSocketListener);
    }

    /**
     * Configures additional client miniserver based on given endpoint.
     *
     * @param clientEndpoint endpoint for client miniserver
     */
    public void addClientMiniserver(final @NotNull LoxoneEndpoint clientEndpoint) {
        clientMiniserverHttp(requireNonNull(clientEndpoint));
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
     * Returns configured {@link LoxoneHttp} for client miniserver of given endpoint. Adds the endpoint to clients,
     * in case it's not there yet.
     *
     * @param clientEndpoint endpoint for client miniserver
     * @return configured {@link LoxoneHttp} for client miniserver
     */
    @NotNull
    public LoxoneHttp clientMiniserverHttp(final @NotNull LoxoneEndpoint clientEndpoint) {
        return clientMiniserversHttp.computeIfAbsent(requireNonNull(clientEndpoint), LoxoneHttp::new);
    }

    /**
     * Iterable of configured client miniservers.
     * @return client miniservers {@link LoxoneHttp}
     */
    @NotNull
    public Iterable<LoxoneHttp> clientMiniserversHttp() {
        return clientMiniserversHttp.values();
    }

    /**
     * Iterable of all configured miniservers - first is the main miniserver and then the clients.
     * @return all miniservers' {@link LoxoneHttp}
     */
    @NotNull
    public Iterable<LoxoneHttp> allMiniserversHttp() {
        final List<LoxoneHttp> all = new ArrayList<>();
        all.add(http());
        all.addAll(clientMiniserversHttp.values());
        return all;
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

    /**
     * Send command built by given commandBuilder applied on given control. Use static factory methods at
     * {@link ControlCommand} or extend it with more specific classes in complex scenarios.
     * Use {@link CommandResponseListener} added to {@link #webSocket()} to process the response.
     * @param control control to build command from
     * @param commandBuilder function returning the command of given control
     * @param <C> type of the control
     */
    public <C extends Control> void sendControlCommand(
            final @NotNull C control, final @NotNull Function<C, ControlCommand<?>> commandBuilder) {
        sendCommand(commandBuilder.apply(control), control.isSecured());
    }

    private void sendControlCommand(final Control control, final String command) {
        requireNonNull(control, "control can't be null");
        final ControlCommand<JsonValue> controlCommand = genericControlCommand(control.getUuid().toString(), command);
        sendCommand(controlCommand, control.isSecured());
    }

    private void sendCommand(final ControlCommand<?> controlCommand, final boolean secured) {
        if (secured) {
            webSocket().sendSecureCommand(controlCommand);
        } else {
            webSocket().sendCommand(controlCommand);
        }
    }

    /**
     * Stops the service closing underlying resources, namely {@link LoxoneWebSocket}.
     *
     * @throws LoxoneException in case the proper close failed
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
            if (appLatch != null) {
                appLatch.countDown();
            }
            loxoneAppListeners.forEach(listener -> listener.onLoxoneApp(loxoneApp));
            return State.READ;
        }

        @Override
        public boolean accepts(final @NotNull Class clazz) {
            return LoxoneApp.class.equals(clazz);
        }
    }
}
