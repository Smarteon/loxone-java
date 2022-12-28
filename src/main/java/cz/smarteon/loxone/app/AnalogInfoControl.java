package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

/**
 * Used for controls accepting an analog signal.
 */
public class AnalogInfoControl extends Control {

    public static final String NAME = "InfoOnlyAnalog";

    @NotNull
    public LoxoneUuid stateValue() {
        return getCompulsoryState("value").only();
    }

    @NotNull
    public LoxoneUuid stateError() {
        return getCompulsoryState("error").only();
    }

}
