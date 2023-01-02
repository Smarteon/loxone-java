package cz.smarteon.loxone.app;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

/**
 * Represents Burglar Alarm block.
 */
public class AlarmControl extends Control {

    public static final String NAME = "Alarm";

    /**
     * @return state referring whether alarm is armed
     */
    @NotNull
    public LoxoneUuid stateArmed() {
        return getCompulsoryState("armed").only();
    }

    /**
     * @return state referring alarm arming delay seconds
     */
    @NotNull
    public LoxoneUuid stateArmedDelay() {
        return getCompulsoryState("armedDelay").only();
    }

    /**
     * @return state referring alarm total arming delay seconds
     */
    @NotNull
    public LoxoneUuid stateArmedDelayTotal() {
        return getCompulsoryState("armedDelayTotal").only();
    }

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
     * @return state referring whether movement is disabled
     */
    @NotNull
    public LoxoneUuid stateDisabledMove() {
        return getCompulsoryState("disabledMove").only();
    }
}
