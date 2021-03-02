package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.LoxoneUuid;

public class PushbuttonControl extends Control {

    public static final String NAME = "Pushbutton";

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
    public void sendPulse(final @NotNull Loxone loxone){
        loxone.sendControlPulse(this);
    }
}
