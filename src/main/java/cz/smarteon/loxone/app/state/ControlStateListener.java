package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.app.Control;
import org.jetbrains.annotations.NotNull;

/**
 * Allows reacting on control state changes.
 * @param <T> The type of control this listener is interested in.
 */
public interface ControlStateListener<S, T extends Control> {

    /**
     * Called when an event is received and processed by the control state and the state of the control has changed.
     * @param controlState the control state that received the event (should not be null)
     */
    void onStateChange(@NotNull ControlState<S, T> controlState);
}
