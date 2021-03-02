package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class JalousieControl extends Control {

    public static final String NAME = "Jalousie";

    @NotNull
    public LoxoneUuid stateUp() {
        return getCompulsoryState("up").only();
    }
    @NotNull
    public LoxoneUuid stateDown() {
        return getCompulsoryState("down").only();
    }
    @NotNull
    public LoxoneUuid statePosition() {
        return getCompulsoryState("position").only();
    }
    @NotNull
    public LoxoneUuid stateShadePosition() {
        return getCompulsoryState("shadePosition").only();
    }
    @NotNull
    public LoxoneUuid stateSafetyActive() {
        return getCompulsoryState("safetyActive").only();
    }
    @NotNull
    public LoxoneUuid stateAutoAllowed() {
        return getCompulsoryState("autoAllowed").only();
    }
    @NotNull
    public LoxoneUuid stateAutoActive() {
        return getCompulsoryState("autoActive").only();
    }
    @NotNull
    public LoxoneUuid stateLocked() {
        return getCompulsoryState("locked").only();
    }
    @NotNull
    public LoxoneUuid stateHasEndposition() {
        return getCompulsoryState("hasEndposition").only();
    }
    @NotNull
    public LoxoneUuid stateMode() {
        return getCompulsoryState("mode").only();
    }
    @NotNull
    public LoxoneUuid stateLearningStep() {
        return getCompulsoryState("learningStep").only();
    }
    @NotNull
    public LoxoneUuid stateInfoText() {
        return getCompulsoryState("infoText").only();
    }
}
