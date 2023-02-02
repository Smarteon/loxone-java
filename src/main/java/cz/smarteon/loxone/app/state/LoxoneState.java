package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.LoxoneAppListener;
import cz.smarteon.loxone.LoxoneEventListener;
import cz.smarteon.loxone.LoxoneException;
import cz.smarteon.loxone.LoxoneProfile;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.app.AnalogInfoControl;
import cz.smarteon.loxone.app.Control;
import cz.smarteon.loxone.app.DigitalInfoControl;
import cz.smarteon.loxone.app.LoxoneApp;
import cz.smarteon.loxone.app.SwitchControl;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main entry point of the library if state retention is needed.
 */
@Slf4j
public class LoxoneState implements LoxoneAppListener, LoxoneEventListener {

    private static final Map<Class<? extends Control>, Class<? extends ControlState<? extends Control>>> SUPPORTED_CONTROLS_STATE_MAP;

    private final Loxone loxone;

    private LoxoneApp loxoneApp;

    private Map<LoxoneUuid, ControlState<?>> controlStates;

    static {
        SUPPORTED_CONTROLS_STATE_MAP = new HashMap<>();
        SUPPORTED_CONTROLS_STATE_MAP.put(SwitchControl.class, SwitchControlState.class);
        SUPPORTED_CONTROLS_STATE_MAP.put(AnalogInfoControl.class, AnalogInfoControlState.class);
        SUPPORTED_CONTROLS_STATE_MAP.put(DigitalInfoControl.class, DigitalInfoControlState.class);
    }

    public LoxoneState(@NotNull LoxoneProfile loxoneProfile) {
        loxone = new Loxone(loxoneProfile);
        loxone.setEventsEnabled(true);
        loxone.registerLoxoneAppListener(this);

    }

    public LoxoneState(@NotNull Loxone loxone) {
        this.loxone = loxone;
        if (!loxone.isEventsEnabled() && loxone.isStarted()) {
            throw new IllegalArgumentException("Loxone object has been started but events are not enabled!");
        }
    }

    @TestOnly
    Map<Class<? extends Control>, Class<? extends ControlState<? extends Control>>> getSupportedControlsStateMap() {
        return SUPPORTED_CONTROLS_STATE_MAP;
    }

    @TestOnly
    Map<LoxoneUuid, ControlState<?>> getControlStates() {
        return controlStates;
    }

    @NotNull
    public Loxone loxone() {
        return loxone;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getStateForControl(Control control) {
        return (T) controlStates.get(control.getUuid());
    }

    @Override
    public void onLoxoneApp(@NotNull LoxoneApp loxoneApp) {
        this.loxoneApp = loxoneApp;
        this.loxone.webSocket().registerListener(this);
        initializeState();
    }

    @Override
    public void onEvent(@NotNull ValueEvent event) {
        controlStates.values().forEach(cs -> cs.accept(event));
    }

    @Override
    public void onEvent(@NotNull TextEvent event) {
        controlStates.values().forEach(cs -> cs.accept(event));
    }

    private void initializeState() {
        controlStates = loxoneApp.getControls().entrySet().stream()
            .filter(e -> SUPPORTED_CONTROLS_STATE_MAP.containsKey(e.getValue().getClass()))
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> {
                        try {
                            return (ControlState<?>) SUPPORTED_CONTROLS_STATE_MAP.get(e.getValue().getClass())
                                    .getConstructors()[0].newInstance(loxone, e.getValue());
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                 | InvocationTargetException ex) {
                            throw new LoxoneException("Problem while initializing state!", ex);
                        }
                    })
            );
    }
}
