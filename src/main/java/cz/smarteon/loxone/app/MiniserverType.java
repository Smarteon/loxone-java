package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The type of the Loxone miniserver
 */
public enum MiniserverType {

    /**
     * Regular full size miniserver 1st gen.
     */
    REGULAR(0),
    /**
     * Miniserver GO
     */
    GO(1),
    /**
     * Regular full size miniserver 2nd gen., produced since Dec 2019
     */
    REGULAR_V2(2),
    /**
     * Miniserver of unknown type.
     */
    UNKNOWN(Integer.MIN_VALUE);

    private final int value;

    MiniserverType(int value) {
        this.value = value;
    }

    @JsonCreator
    public static MiniserverType fromValue(final int value) {
        MiniserverType result = UNKNOWN;
        for (MiniserverType type : values()) {
            if (type.value == value)
                result = type;
        }

        return result;
    }
}
