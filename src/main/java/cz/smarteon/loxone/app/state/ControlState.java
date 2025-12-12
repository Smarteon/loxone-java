package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.Control;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Base class for all the controlStates in loxone application.
 * <p>
 * This class keeps track of the state of the control based on the events of the miniserver.
 * </p>
 * @param <S> The type of the state.
 * @param <T> The type of control this class keeps track of.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ControlState<S, T extends Control> {

    /**
     * The webSocket connection to communicate with the loxone miniserver.
     */
    @Getter
    private final Loxone loxone;

    /**
     * The control that this state refers to.
     */
    @Getter
    private final T control;

    /**
     * The current state of the control.
     */
    @Getter
    private S state;

    /**
     * The set of listeners registered for this control state.
     */
    private final Set<ControlStateListener<S, T>> listeners = new CopyOnWriteArraySet<>();

    /**
     * Registers a listener to be notified of state changes.
     * @param listener the listener to register (should not be null).
     */
    public void registerListener(@NotNull ControlStateListener<S, T> listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a previously registered listener.
     * @param listener the listener to unregister (should not be null).
     */
    public void unregisterListener(@NotNull ControlStateListener<S, T> listener) {
        listeners.remove(listener);
    }

    /**
     * Method that accepts ValueEvent from the miniserver to update the internal state.
     * @param event value event received (should not be null).
     */
    void accept(@NotNull ValueEvent event) {
        // default implementation
    }

    /**
     * Method that accepts TextEvent from the miniserver to update the internal state.
     * @param event text event received (should not be null).
     */
    void accept(@NotNull TextEvent event) {
        // default implementation
    }

    void setState(@NotNull S state) {
        boolean stateChanged = this.state == null || !this.state.equals(state);
        this.state = state;
        if (stateChanged) {
            notifyStateChanged();
        }
    }

    /**
     * Notifies all registered listeners of a state change.
     */
    void notifyStateChanged() {
        for (ControlStateListener<S, T> listener : listeners) {
            listener.onStateChange(this);
        }
    }
}
