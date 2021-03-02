package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import cz.smarteon.loxone.LoxoneUuid;

public class InfoOnlyAnalogControl extends Control {

    public static final String NAME = "InfoOnlyAnalog";

    @NotNull
    public String detailFormat() {
        return (String)getCompulsoryDetail("format");
    }

    @NotNull
    public LoxoneUuid stateValue() {
        return getCompulsoryState("value").only();
    }
    @NotNull
    public LoxoneUuid stateError() {
        return getCompulsoryState("error").only();
    }
}
