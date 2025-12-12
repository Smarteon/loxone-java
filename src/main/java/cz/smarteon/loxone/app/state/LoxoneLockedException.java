package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.LoxoneException;

public class LoxoneLockedException extends LoxoneException {

    public LoxoneLockedException(String message) {
        super(message);
    }
}
