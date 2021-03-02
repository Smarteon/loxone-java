package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class LightControllerControl extends Control {

    public static final String NAME = "LightController";

    @NotNull
    public int detailMovementScene() {
        return (int)getCompulsoryDetail("movementScene");
    }
    @NotNull
    public LoxoneUuid stateActiveScene() {
        return getCompulsoryState("activeScene").only();
    }
    @NotNull
    public LoxoneUuid stateSceneList() {
        return getCompulsoryState("sceneList").only();
    }
}
