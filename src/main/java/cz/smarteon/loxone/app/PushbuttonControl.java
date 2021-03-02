package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class PushbuttonControl extends Control {

    public static final String NAME = "Pushbutton";

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }
}
