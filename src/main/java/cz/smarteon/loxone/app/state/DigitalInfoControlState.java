package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.DigitalInfoControl;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * State class for keeping state of a <code>DigitalInfoControl</code>.
 */
public class DigitalInfoControlState extends ControlState<DigitalInfoControl> {

    /**
     * Current value of the DigitalInfoControl.
     */
    @Getter
    @Nullable
    private Boolean state;


    public DigitalInfoControlState(Loxone loxone, DigitalInfoControl control) {
        super(loxone, control);
    }

    /**
     * Accepts ValueEvents that can contain active state updates for this control.
     * @param event value event received (should not be null)
     */
    @Override
    void accept(@NotNull ValueEvent event) {
        super.accept(event);
        if (event.getUuid().equals(control.stateActive())) {
            processActiveEvent(event);
        }
    }

    /**
     * Process the ValueEvent as an active state event message and update the state of the control accordingly.
     * @param event value event received
     */
    private void processActiveEvent(ValueEvent event) {
        double value = event.getValue();
        if (value == 0) {
            state = false;
            return;
        } else if (value == 1) {
            state = true;
            return;
        }
        throw new IllegalArgumentException("Value " + value + " not known for DigitalInfoControl");
    }
}
