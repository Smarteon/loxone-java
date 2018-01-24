package cz.smarteon.loxone.config;

import cz.smarteon.loxone.LoxoneUuid;

public class SwitchControl extends Control {

    public static final String NAME = "Switch";

    public LoxoneUuid getActive() {
        return getCompulsoryState("active").only();
    }
}
