package cz.smarteon.loxone.app;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

public class SwitchControl extends Control {

    public static final String NAME = "Switch";

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }

    public void sendOn(final @NotNull Loxone loxone){
        loxone.sendControlOn(this);
    }
    public void sendOff(final @NotNull Loxone loxone){
        loxone.sendControlOff(this);
    }
}
