package cz.smarteon.loxone.config;

import cz.smarteon.loxone.LoxoneUuid;

public class AlarmControl extends Control {

    public static final String NAME = "Alarm";

    public LoxoneUuid getArmed() {
        return getCompulsoryState("armed").only();
    }
}
