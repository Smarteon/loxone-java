package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class TextInputControl extends Control {

    public static final String NAME = "TextInput";

    @NotNull
    public LoxoneUuid stateTextAndIcon() {
        return getCompulsoryState("text").only();
    }
}
