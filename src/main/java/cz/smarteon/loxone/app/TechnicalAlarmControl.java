package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneNotDocumented;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.LoxoneUuids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class TechnicalAlarmControl extends Control {

    public static final String NAME = "SmokeAlarm";

    /**
     * @return state referring current alarm level
     */
    @NotNull
    public LoxoneUuid stateLevel() {
        return getCompulsoryState("level").only();
    }

    /**
     * @return state referring next alarm level
     */
    @NotNull
    public LoxoneUuid stateNextLevel() {
        return getCompulsoryState("nextLevel").only();
    }

    /**
     * @return state referring next alarm level delay seconds
     */
    @NotNull
    public LoxoneUuid stateNextLevelDelay() {
        return getCompulsoryState("nextLevelDelay").only();
    }

    /**
     * @return state referring total next alarm level delay seconds
     */
    @NotNull
    public LoxoneUuid stateNextLevelDelayTotal() {
        return getCompulsoryState("nextLevelDelayTotal").only();
    }

    /**
     * @return state referring triggered sensors
     */
    @NotNull
    public LoxoneUuid stateSensors() {
        return getCompulsoryState("sensors").only();
    }

    /**
     * @return state referring alarm triggered start time
     */
    @NotNull
    public LoxoneUuid stateStartTime() {
        return getCompulsoryState("startTime").only();
    }

    /**
     * @return state referring whether alarm is acoustic
     */
    @NotNull
    public LoxoneUuid stateAcousticAlarm() {
        return getCompulsoryState("acousticAlarm").only();
    }

    /**
     * @return state referring whether alarm is testing
     */
    @NotNull
    public LoxoneUuid stateTestAlarm() {
        return getCompulsoryState("testAlarm").only();
    }

    /**
     * @return state referring alarm cause
     */
    @NotNull
    public LoxoneUuid stateAlarmCause() {
        return getCompulsoryState("alarmCause").only();
    }

    @LoxoneNotDocumented
    @Nullable
    public LoxoneUuid stateTimeServiceMode() {
        return ofNullable(getStates()).map(states -> states.get("timeServiceMode")).map(LoxoneUuids::only).orElse(null);
    }

    @LoxoneNotDocumented
    @Nullable
    public LoxoneUuid stateAreAlarmSignalsOff() {
        return ofNullable(getStates()).map(states -> states.get("areAlarmSignalsOff")).map(LoxoneUuids::only).orElse(null);
    }

}
