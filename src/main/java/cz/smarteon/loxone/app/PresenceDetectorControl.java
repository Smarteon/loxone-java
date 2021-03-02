package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import cz.smarteon.loxone.LoxoneNotDocumented;
import cz.smarteon.loxone.LoxoneUuid;

/**
 * Represents presence block.
 */
@LoxoneNotDocumented
public class PresenceDetectorControl extends Control {

    public static final String NAME = "PresenceDetector";

    @NotNull
    public String detailText_On() {
        return ((Map<String,String>)getCompulsoryDetail("text")).get("on");
    }
    @NotNull
    public String detailText_Off() {
        return ((Map<String,String>)getCompulsoryDetail("text")).get("off");
    }

    @NotNull
    public LoxoneUuid stateActive() {
        return getCompulsoryState("active").only();
    }
    @NotNull
    public LoxoneUuid stateLocked() {
        return getCompulsoryState("locked").only();
    }
    @NotNull
    public LoxoneUuid stateActiveSince() {
        return getCompulsoryState("activeSince").only();
    }
    @NotNull
    public LoxoneUuid stateInfoText() {
        return getCompulsoryState("infoText").only();
    }
}
