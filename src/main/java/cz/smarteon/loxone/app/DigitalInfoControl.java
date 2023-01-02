package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

/**
 * Used for controls accepting a digital signal.
 */
public class DigitalInfoControl extends Control {

    public static final String NAME = "InfoOnlyDigital";

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }

}
