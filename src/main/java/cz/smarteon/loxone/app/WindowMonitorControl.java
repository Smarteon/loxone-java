package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Map;

import cz.smarteon.loxone.LoxoneUuid;

public class WindowMonitorControl extends Control {

    public static final String NAME = "WindowMonitor";

    @NotNull
    public Map<String,String>[] detailWindows() {
        return (Map<String,String>[])getCompulsoryDetail("text");
    }
    @NotNull
    public LoxoneUuid stateWindowStates() {
        return getCompulsoryState("windowStates").only();
    }
    @NotNull
    public LoxoneUuid stateNumOpen() {
        return getCompulsoryState("numOpen").only();
    }
    @NotNull
    public LoxoneUuid stateNumClosed() {
        return getCompulsoryState("numClosed").only();
    }
    @NotNull
    public LoxoneUuid stateNumTilted() {
        return getCompulsoryState("numTilted").only();
    }
    @NotNull
    public LoxoneUuid stateNumOffline() {
        return getCompulsoryState("numOffline").only();
    }
    @NotNull
    public LoxoneUuid stateNummLocked() {
        return getCompulsoryState("nummLocked").only();
    }
    @NotNull
    public LoxoneUuid stateNumUnlocked() {
        return getCompulsoryState("numUnlocked").only();
    }
    @NotNull
    public LoxoneUuid stateAdditionalMoods() {
        return getCompulsoryState("additionalMoods").only();
    }
}
