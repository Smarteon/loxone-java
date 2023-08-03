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
 * Represents a calendar entry for non-repeating period.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntryPeriod extends CalEntryBase {

    public static final String CAL_MODE = "3";

    /**
     * Provided as int e.g. 2024.
     */
    private int startYear;

    /**
     * Provided as int 0 to 12.
     */
    private int startMonth;

    /**
     * Provided as int 0 to 31.
     */
    private int startDay;

    /**
     * Provided as int e.g. 2024.
     */
    private int endYear;

    /**
     * Provided as int 0 to 12.
     */
    private int endMonth;

    /**
     * Provided as int 0 to 31.
     */
    private int endDay;

    @JsonCreator
    public CalEntryPeriod(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "startYear") int startYear,
            @JsonProperty(value = "startMonth") int startMonth,
            @JsonProperty(value = "startDay") int startDay,
            @JsonProperty(value = "endYear") int endYear,
            @JsonProperty(value = "endMonth") int endMonth,
            @JsonProperty(value = "endDay") int endDay
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    public CalEntryPeriod(
            @NotNull String name,
            int operatingMode,
            int startYear,
            int startMonth,
            int startDay,
            int endYear,
            int endMonth,
            int endDay
    ) {
        super(name, operatingMode, CAL_MODE);
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    public CalendarCommand<EmptyValue> createEntryCommand(){
        return new CalendarCommand<>(CREATE_ENTRY + name + "/" + operatingMode + "/" + CAL_MODE + "/" + startYear + "/" + startMonth + "/" + startDay + "/" + endYear + "/" + endMonth + "/" + endDay, EmptyValue.class);
    }
}
