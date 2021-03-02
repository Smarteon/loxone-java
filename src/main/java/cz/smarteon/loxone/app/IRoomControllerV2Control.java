package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class IRoomControllerV2Control extends Control {

    public static final String NAME = "IRoomControllerV2";

    @NotNull
    public LoxoneUuid stateActiveMode() {
        return getCompulsoryState("activeMode").only();
    }
    @NotNull
    public LoxoneUuid stateOperatingMode() {
        return getCompulsoryState("operatingMode").only();
    }
    @NotNull
    public LoxoneUuid stateOverrideEntries() {
        return getCompulsoryState("overrideEntries").only();
    }
    @NotNull
    public LoxoneUuid statePrepareState() {
        return getCompulsoryState("prepareState").only();
    }
    @NotNull
    public LoxoneUuid stateOverrideReason() {
        return getCompulsoryState("overrideReason").only();
    }
    @NotNull
    public LoxoneUuid stateTempActual() {
        return getCompulsoryState("tempActual").only();
    }
    @NotNull
    public LoxoneUuid stateTempTarget() {
        return getCompulsoryState("tempTarget").only();
    }
    @NotNull
    public LoxoneUuid stateComfortTemperature() {
        return getCompulsoryState("comfortTemperature").only();
    }
    @NotNull
    public LoxoneUuid stateComfortTolerance() {
        return getCompulsoryState("comfortTolerance").only();
    }
    @NotNull
    public LoxoneUuid stateAbsentMinOffset() {
        return getCompulsoryState("absentMinOffset").only();
    }
    @NotNull
    public LoxoneUuid stateAbsentMaxOffset() {
        return getCompulsoryState("absentMaxOffset").only();
    }
    @NotNull
    public LoxoneUuid stateFrostProtectTemperature() {
        return getCompulsoryState("frostProtectTemperature").only();
    }
    @NotNull
    public LoxoneUuid stateHeatProtectTemperature() {
        return getCompulsoryState("heatProtectTemperature").only();
    }
    @NotNull
    public LoxoneUuid stateComfortTemperatureOffset() {
        return getCompulsoryState("comfortTemperatureOffset").only();
    }
    @NotNull
    public LoxoneUuid stateOpenWindow() {
        return getCompulsoryState("openWindow").only();
    }
}
