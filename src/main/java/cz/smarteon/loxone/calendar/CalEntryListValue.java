package cz.smarteon.loxone.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode a list of calendar entries.
 */
@Getter
public class CalEntryListValue implements LoxoneValue {

    private final List<CalEntryBase> events;

    @JsonCreator
    protected CalEntryListValue(String events) throws IOException {
        this.events = Codec.readList(events, CalEntryBase.class);
    }
}
