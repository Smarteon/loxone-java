package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneValue;

public interface CommandListener {
    enum State {
        READ {
            @Override
            State fold(State next) {
                return next != IGNORED ? next : this;
            }
        },
        CONSUMED {
            @Override
            State fold(State next) {
                return this;
            }
        },
        IGNORED {
            @Override
            State fold(State next) {
                return next;
            }
        };

        abstract State fold(State next);
    }

    State onCommand(String command, LoxoneValue value);
}
