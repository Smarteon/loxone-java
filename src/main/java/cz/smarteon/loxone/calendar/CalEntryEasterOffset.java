package cz.smarteon.loxone.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.user.EmptyValue;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a calendar entry repeating each year based on Easter.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntryEasterOffset extends CalEntryBase {

    public static final String CAL_MODE = "1";

    /**
     * Provided as int representing a day offset to the end of Easter e.g. 2 = a day 2 days after the end of Easter.
     */
    private int easterOffset;

    @JsonCreator
    public CalEntryEasterOffset(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "easterOffset") int easterOffset
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.easterOffset = easterOffset;
    }

    public CalEntryEasterOffset(
            @NotNull String name,
            int operatingMode,
            int easterOffset
    ) {
        super(name, operatingMode, CAL_MODE);
        this.easterOffset = easterOffset;
    }

    public CalendarCommand<EmptyValue> createEntryCommand(){
        return new CalendarCommand<>(CREATE_ENTRY + name + "/" + operatingMode + "/" + CAL_MODE + "/" + easterOffset, EmptyValue.class);
    }
}
