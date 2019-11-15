package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MiniserverType {

    REGULAR(0),
    GO(1);

    private final int value;

    MiniserverType(int value) {
        this.value = value;
    }

    @JsonCreator
    public static MiniserverType fromValue(final int value) {
        for (MiniserverType type : values()) {
            if (type.value == value)
                return type;
        }

        throw new IllegalArgumentException("Invalid miniserver type specification, value=" + value);
    }
}
