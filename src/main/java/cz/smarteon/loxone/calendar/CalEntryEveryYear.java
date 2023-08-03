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
 * Represents a calendar entry repeating each year.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntryEveryYear extends CalEntryBase {

    public static final String CAL_MODE = "0";

    /**
     * Provided as int 0 to 12.
     */
    private int startMonth;

    /**
     * Provided as int 0 to 31.
     */
    private int startDay;

    @JsonCreator
    public CalEntryEveryYear(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "startMonth") int startMonth,
            @JsonProperty(value = "startDay") int startDay
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    public CalEntryEveryYear(
            String name,
            int operatingMode,
            int startMonth,
            int startDay
    ) {
        super(name, operatingMode, CAL_MODE);
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    public CalendarCommand<EmptyValue> createEntryCommand(){
        return new CalendarCommand<>(CREATE_ENTRY + name + "/" + operatingMode + "/" + CAL_MODE + "/" + startMonth + "/" + startDay, EmptyValue.class);
    }
}
