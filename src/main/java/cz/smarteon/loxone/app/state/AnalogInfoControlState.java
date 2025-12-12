package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.AnalogInfoControl;
import cz.smarteon.loxone.message.ValueEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * State class for keeping state of a <code>AnalogInfoControl</code>.
 */
public class AnalogInfoControlState extends ControlState<Double, AnalogInfoControl> {

    public AnalogInfoControlState(Loxone loxone, AnalogInfoControl control) {
        super(loxone, control);
    }

    /**
     * Accepts ValueEvents that can contain active state updates for this control.
     * @param event value event received (should not be null)
     */
    @Override
    void accept(@NotNull ValueEvent event) {
        if (event.getUuid().equals(getControl().stateValue())) {
            setState(event.getValue());
        }
    }
}
