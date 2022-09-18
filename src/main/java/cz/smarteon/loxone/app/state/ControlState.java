package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.Control;
import cz.smarteon.loxone.message.ControlCommand;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all the controlStates in loxone application
 */
@RequiredArgsConstructor
public abstract class ControlState<T extends Control> {

    /**
     * The webSocket connection to communicate with the loxone miniserver.
     */
    protected final Loxone loxone;

    /**
     * The control that this state refers to.
     */
    protected final T control;

    /**
     * Method that accepts ValueEvent from the miniserver to update the internal state.
     * @param event value event received (should not be null)
     */
    void accept(@NotNull ValueEvent event) {
        // default implementation
    }

    /**
     * Method that accepts TextEvent from the miniserver to update the internal state.
     * @param event text event received (should not be null)
     */
    void accept(@NotNull TextEvent event) {
        // default implementation
    }

    /**
     * Method to update the state of the command in the miniserver.
     * @param controlCommand that needs to be sent
     */
    protected void sendCommand(ControlCommand<?> controlCommand) {
        if (control.isSecured()) {
            loxone.webSocket().sendSecureCommand(controlCommand);
        } else {
            loxone.webSocket().sendCommand(controlCommand);
        }
    }
}
