package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import cz.smarteon.loxone.LoxoneUuid;

public class InfoOnlyDigitalControl extends Control {

    public static final String NAME = "InfoOnlyDigital";

    @NotNull
    public String detailText_On() {
        return ((Map<String,String>)getCompulsoryDetail("text")).get("on");
    }
    @NotNull
    public String detailText_Off() {
        return ((Map<String,String>)getCompulsoryDetail("text")).get("off");
    }
    @NotNull
    public String detailColor_On() {
        return ((Map<String,String>)getCompulsoryDetail("color")).get("on");
    }
    @NotNull
    public String detailColor_Off() {
        return ((Map<String,String>)getCompulsoryDetail("color")).get("off");
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
