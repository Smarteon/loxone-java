package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DateValue implements LoxoneValue {

    private final Date date;

    private DateValue(Date date) {
        this.date = date;
    }

    @JsonCreator
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    public static DateValue create(Date date) {
        return new DateValue(date);
    }

    public Date getDate() {
        return date;
    }
}
