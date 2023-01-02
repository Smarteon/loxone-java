package cz.smarteon.loxone.message;

/**
 * Represents the type of a loxone message.
 */
public enum MessageKind {
    TEXT, FILE, EVENT_VALUE, EVENT_TEXT, EVENT_DAYTIMER, OUT_OF_SERVICE, KEEP_ALIVE, EVENT_WEATHER;

    public static MessageKind valueOf(byte byteValue) {
        return values()[(int) byteValue];
    }
}
