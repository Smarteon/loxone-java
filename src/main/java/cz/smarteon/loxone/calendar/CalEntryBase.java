package cz.smarteon.loxone.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.user.EmptyValue;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Base class for every calendar entry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "calMode")
@JsonSubTypes({
        @JsonSubTypes.Type(name = CalEntrySingleDay.CAL_MODE, value = CalEntrySingleDay.class),
        @JsonSubTypes.Type(name = CalEntryDayOfWeek.CAL_MODE, value = CalEntryDayOfWeek.class),
        @JsonSubTypes.Type(name = CalEntryPeriodYearly.CAL_MODE, value = CalEntryPeriodYearly.class),
        @JsonSubTypes.Type(name = CalEntryEasterOffset.CAL_MODE, value = CalEntryEasterOffset.class),
        @JsonSubTypes.Type(name = CalEntryPeriod.CAL_MODE, value = CalEntryPeriod.class),
        @JsonSubTypes.Type(name = CalEntryEveryYear.CAL_MODE, value = CalEntryEveryYear.class)
})
public abstract class CalEntryBase {

    protected static final String CREATE_ENTRY = "calendarcreateentry/";
    protected static final String DELETE_ENTRY = "calendardeleteentry/";
    protected static final String UPDATE_ENTRY = "calendarupdateentry/";

    /**
     * UUID of this calendar entry, should be unique.
     */
    protected LoxoneUuid uuid;

    /**
     * Calendar entry name - usually localized, non unique.
     */
    @NotNull
    @Setter
    protected String name;

    /**
     * Determines which user mode this calendar entry controls e.g. Vacation = 1, Holiday = 0.
     */
    @Setter
    protected int operatingMode;

    /**
     * Calendar entry mode
     * <pre>
     * 0 - One day every year
     * 1 - One day dependent on Easter
     * 2 - One day
     * 3 - Period
     * 4 - Period every year
     * 5 - A day in a week
     * </pre>
     */
    protected int calMode;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    protected CalEntryBase(
            @JsonProperty("uuid") @NotNull LoxoneUuid uuid,
            @JsonProperty("name") @NotNull String name,
            @JsonProperty("operatingMode") int operatingMode,
            @JsonProperty("calMode") @NotNull String calMode) {
        this.uuid = uuid;
        this.name = name;
        this.operatingMode = operatingMode;
        this.calMode = Integer.parseInt(calMode);
    }

    protected CalEntryBase(@NotNull String name, int operatingMode, @NotNull String calMode) {
        this.uuid = null;
        this.name = name;
        this.operatingMode = operatingMode;
        this.calMode = Integer.parseInt(calMode);
    }

    /**
     * Creates delete calendar entry command.
     * Command deletes a calendar entry with a given uuid.
     *
     * @return delete calendar entry command
     * @throws NullPointerException user has to have UUID
     */
    public CalendarCommand<EmptyValue> deleteEntryCommand(){
        requireNonNull(uuid, "Cal entry UUID cannot be null when deleting it");
        return new CalendarCommand<>(DELETE_ENTRY + uuid, EmptyValue.class);
    }

    /**
     * Creates create calendar entry command.
     * Command creates a new calendar entry with the given parameters.
     *
     * @return create calendar entry command
     */
    public CalendarCommand<EmptyValue> createEntryCommand() {
        return new CalendarCommand<>(CREATE_ENTRY + this, EmptyValue.class);
    }

    /**
     * Creates update calendar entry command.
     * Command updates an existing calendar entry with the given parameters.
     *
     * @return update calendar entry command
     * @param uuid - uuid of an existing entry to be updated
     */
    public CalendarCommand<EmptyValue> updateEntryCommand(LoxoneUuid uuid) {
        return new CalendarCommand<>(UPDATE_ENTRY + uuid + "/" + this, EmptyValue.class);
    }
}
