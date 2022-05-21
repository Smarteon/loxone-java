package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

public class DigitalInfoControl extends Control {

    public static final String NAME = "InfoOnlyDigital";

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }

}
