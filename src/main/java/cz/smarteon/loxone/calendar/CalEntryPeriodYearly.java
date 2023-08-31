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
 * Represents a calendar entry for yearly repeating period.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntryPeriodYearly extends CalEntryBase {

    public static final String CAL_MODE = "4";

    /**
     * Provided as int 1 to 12.
     */
    private int startMonth;

    /**
     * Provided as int 1 to 31.
     */
    private int startDay;

    /**
     * Provided as int 1 to 12.
     */
    private int endMonth;

    /**
     * Provided as int 1 to 31.
     */
    private int endDay;

    @JsonCreator
    public CalEntryPeriodYearly(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "startMonth") int startMonth,
            @JsonProperty(value = "startDay") int startDay,
            @JsonProperty(value = "endMonth") int endMonth,
            @JsonProperty(value = "endDay") int endDay
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    public CalEntryPeriodYearly(
            @NotNull String name,
            int operatingMode,
            int startMonth,
            int startDay,
            int endMonth,
            int endDay
    ) {
        super(name, operatingMode, CAL_MODE);
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    public CalendarCommand<EmptyValue> createEntryCommand(){
        return new CalendarCommand<>(CREATE_ENTRY + name + "/" + operatingMode + "/" + CAL_MODE + "/" + startMonth + "/" + startDay + "/" + endMonth + "/" + endDay, EmptyValue.class);
    }
}
