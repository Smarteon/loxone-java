package cz.smarteon.loxone.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.user.EmptyValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a calendar entry for a repeating day of week in a month.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntryDayOfWeek extends CalEntryBase{

    public static final String CAL_MODE = "5";

    /**
     * Enum class containing constants for weekDayInMonth attribute.
     */
    @AllArgsConstructor
    public enum WeekDayInMonth {
        EVERY_WEEKDAY(0),
        FIRST_WEEKDAY(1),
        SECOND_WEEKDAY(2),
        THIRD_WEEKDAY(3),
        FOURTH_WEEKDAY(4),
        LAST_WEEKDAY(5);

        private final int value;
    }

    /**
     * Provided as int 1 to 12.
     */
    private int startMonth;

    /**
     * Provided as int 1 to 7.
     */
    private int weekDay;

    /**
     * Provided as int 0 to 5.
     * 0 - Every weekDay in month
     * 1 - First weekDay in month
     * 2 - Second weekDay in month
     * 3 - Third weekDay in month
     * 4 - Fourth weekDay in month
     * 5 - Last weekDay in month */
    private int weekDayInMonth;

    @JsonCreator
    public CalEntryDayOfWeek(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "weekDay") int weekDay,
            @JsonProperty(value = "startMonth") int startMonth,
            @JsonProperty(value = "weekDayInMonth") int weekDayInMonth
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.weekDay = weekDay;
        this.startMonth = startMonth;
        this.weekDayInMonth = weekDayInMonth;
    }

    public CalEntryDayOfWeek(
            @NotNull String name,
            int operatingMode,
            int weekDay,
            int startMonth,
            @NotNull WeekDayInMonth weekDayInMonth
    ) {
        super(name, operatingMode, CAL_MODE);
        this.weekDay = weekDay;
        this.startMonth = startMonth;
        this.weekDayInMonth = weekDayInMonth.value;
    }

    public CalendarCommand<EmptyValue> createEntryCommand(){
        return new CalendarCommand<>(CREATE_ENTRY + name + "/" + operatingMode + "/" + CAL_MODE + "/" + weekDayInMonth + "/" + weekDay + "/" + startMonth, EmptyValue.class);
    }
}
