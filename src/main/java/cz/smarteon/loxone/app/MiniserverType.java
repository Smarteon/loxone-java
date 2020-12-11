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

    /**
     * All miniserver types (including unknown).
     */
    public static final MiniserverType[] ALL = MiniserverType.values();

    /**
     * All known (supported) miniserver types.
     */
    public static final MiniserverType[] KNOWN = new MiniserverType[] { REGULAR, GO, REGULAR_V2 };

    /**
     * Miniserver types of so called first generation (Go and regular first generation).
     */
    public static final MiniserverType[] FIRST_GEN = new MiniserverType[] { REGULAR, GO };
}
