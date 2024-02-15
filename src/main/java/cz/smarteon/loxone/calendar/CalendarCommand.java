package cz.smarteon.loxone.calendar;

import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.app.MiniserverType;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.LoxoneValue;
import cz.smarteon.loxone.user.EmptyValue;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class CalendarCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String GET_ENTRIES = "calendargetentries/";
    private static final String DELETE_ENTRY = "calendardeleteentry/";

    protected static final String COMMAND_PREFIX = "jdev/sps/";

    protected CalendarCommand(String operation, Class<V> valueType) {
        super(requireNonNull(operation, "operation can't be null"),
                Type.JSON, valueType, true, true, MiniserverType.KNOWN);
    }

    /**
     * Creates get calendar entries command.
     * Command returns a list of all calendar entries.
     *
     * @return get calendar entries command
     */
    @NotNull
    public static CalendarCommand<CalEntryListValue> getEntries() {
        return new CalendarCommand<>(GET_ENTRIES, CalEntryListValue.class);
    }

    /**
     * Creates delete calendar entry command.
     * Command deletes a calendar entry with the corresponding lox UUID.
     *
     * @param entryUuid - Lox UUID of a calendar entry
     */
    @NotNull
    public static CalendarCommand<EmptyValue> deleteEntry(LoxoneUuid entryUuid) {
        return new CalendarCommand<>(DELETE_ENTRY + entryUuid, EmptyValue.class);
    }

    @Override
    public String getCommand() {
        return COMMAND_PREFIX + super.getCommand();
    }
}
