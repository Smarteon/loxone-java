package cz.smarteon.loxone;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utilities supporting Loxone miniserver specific time handling.
 */
public abstract class LoxoneTime {

    /**
     * Unix epoch seconds representing the beginning of Loxone epoch.
     */
    public static final int LOXONE_EPOCH_BEGIN = 1230768000;

    /**
     * Converts the given loxone epoch seconds to unix epoch.
     * @param loxSeconds seconds since loxone epoch begin
     * @return unix epoch seconds of given loxone seconds
     */
    public static long getUnixEpochSeconds(long loxSeconds) {
        return LOXONE_EPOCH_BEGIN + loxSeconds;
    }

    /**
     * Get human understandable date and time given Loxone epoch seconds in current time offset.
     *
     * @param loxSeconds seconds since loxone epoch begin
     * @return local date and time of given loxone seconds
     */
    public static LocalDateTime getLocalDateTime(long loxSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(getUnixEpochSeconds(loxSeconds)), ZoneId.systemDefault());
    }
}
