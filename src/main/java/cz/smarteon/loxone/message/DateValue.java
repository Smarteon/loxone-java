package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Date;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode date value.
 */
public final class DateValue implements LoxoneValue {

    private final Date date;

    private DateValue(Date date) {
        this.date = date;
    }

    @JsonCreator
    public static DateValue create(Date date) {
        return new DateValue(date);
    }

    public Date getDate() {
        return date;
    }
}
