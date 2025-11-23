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
public class DigitalInfoControlState extends ControlState<Boolean,DigitalInfoControl> {

    public DigitalInfoControlState(Loxone loxone, DigitalInfoControl control) {
        super(loxone, control);
    }

    /**
     * Accepts ValueEvents that can contain active state updates for this control.
     * @param event value event received (should not be null)
     */
    @Override
    void accept(@NotNull ValueEvent event) {
        if (event.getUuid().equals(getControl().stateActive())) {
            setState(event.getValue() == 1);
        }
    }
}
