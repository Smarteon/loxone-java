package cz.smarteon.loxone.calendar;

import cz.smarteon.loxone.app.MiniserverType;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.LoxoneValue;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class CalendarCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String GET_ENTRIES = "calendargetentries/";

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

    @Override
    public String getCommand() {
        return COMMAND_PREFIX + super.getCommand();
    }
}
