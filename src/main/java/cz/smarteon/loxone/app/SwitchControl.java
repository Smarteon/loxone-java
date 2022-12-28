package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a switch block.
 */
public class SwitchControl extends Control {

    public static final String NAME = "Switch";

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }
}
