package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class TextStateControl extends Control {

    public static final String NAME = "TextState";

    @NotNull
    public LoxoneUuid stateTextAndIcon() {
        return getCompulsoryState("textAndIcon").only();
    }
}
