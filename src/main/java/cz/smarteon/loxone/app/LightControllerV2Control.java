package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class LightControllerV2Control extends Control {

    public static final String NAME = "LightControllerV2";

    @NotNull
    public LoxoneUuid stateActiveMoods() {
        return getCompulsoryState("activeMoods").only();
    }
    @NotNull
    public LoxoneUuid stateMoodList() {
        return getCompulsoryState("moodList").only();
    }
    @NotNull
    public LoxoneUuid stateFavoriteMoods() {
        return getCompulsoryState("favoriteMoods").only();
    }
    @NotNull
    public LoxoneUuid stateAdditionalMoods() {
        return getCompulsoryState("additionalMoods").only();
    }
}
