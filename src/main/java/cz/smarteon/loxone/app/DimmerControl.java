package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class DimmerControl extends Control {

    public static final String NAME = "Dimmer";

    @NotNull
    public LoxoneUuid statePosition() {
        return getCompulsoryState("position").only();
    }
    @NotNull
    public LoxoneUuid stateMin() {
        return getCompulsoryState("min").only();
    }
    @NotNull
    public LoxoneUuid stateMax() {
        return getCompulsoryState("max").only();
    }
    @NotNull
    public LoxoneUuid stateStep() {
        return getCompulsoryState("step").only();
    }
}
