package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.SwitchControl;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand;

/**
 * State class for keeping and managing state of a <code>SwitchControl</code>.
 */
public class SwitchControlState extends LockableControlState<SwitchControl> {

    /**
     * Current state of the SwitchControl.
     */
    @Getter
    @Nullable
    private Boolean state;

    public SwitchControlState(Loxone loxone, SwitchControl control) {
        super(loxone, control);
    }

    /**
     * Toggles state of SwitchControl. When current state is <code>SwitchState.UNINITIALIZED</code> it switches to On.
     */
    public void toggleState() {
        if (Boolean.TRUE.equals(state)) {
            stateOff();
        } else {
            stateOn();
        }
    }

    /**
     * Sets state of SwitchControl to On.
     */
    public void stateOn() {
        loxone.sendControlCommand(control, switchControl -> genericControlCommand(switchControl.getUuid().toString(),
                "On"));
    }

    /**
     * Sets state of SwitchControl to Off.
     */
    public void stateOff() {
        loxone.sendControlCommand(control, switchControl -> genericControlCommand(switchControl.getUuid().toString(),
                "Off"));
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
        state = event.getValue() == 1;
    }
}
