package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class RadioControl extends Control {

    public static final String NAME = "Radio";

    @NotNull
    public LoxoneUuid stateActiveOutput() {
        return getCompulsoryState("activeOutput").only();
    }
}
