package cz.smarteon.loxone.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a calendar entry for one single day.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalEntrySingleDay extends CalEntryBase{

    public static final String CAL_MODE = "2";

    /**
     * Provided as int e.g. 2024.
     */
    private int startYear;

    /**
     * Provided as int 1 to 12.
     */
    private int startMonth;

    /**
     * Provided as int 1 to 31.
     */
    private int startDay;

    @JsonCreator
    public CalEntrySingleDay(
            @JsonProperty(value = "uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "operatingMode") int operatingMode,
            @JsonProperty(value = "startYear") int startYear,
            @JsonProperty(value = "startMonth") int startMonth,
            @JsonProperty(value = "startDay") int startDay
    ) {
        super(uuid, name, operatingMode, CAL_MODE);
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    public CalEntrySingleDay(
            @NotNull String name,
            int operatingMode,
            int startYear,
            int startMonth,
            int startDay
    ) {
        super(name, operatingMode, CAL_MODE);
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    @Override
    public String toString() {
        return name + "/" + operatingMode + "/" + CAL_MODE + "/" + startYear + "/" + startMonth + "/" + startDay;
    }
}
