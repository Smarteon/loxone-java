package cz.smarteon.loxone.calendar;

import cz.smarteon.loxone.message.LoxoneValue;

import java.util.ArrayList;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode a list of calendar entries.
 */
public class CalEntryListValue extends ArrayList<CalEntryBase> implements LoxoneValue { }
