package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;

import static cz.smarteon.loxone.PercentDoubleAdapter.stripPercent;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode integer percentage.
 */
public class PercentValue extends IntValue {

    @JsonCreator
    PercentValue(final String value) {
        // in case of null / missing property in JSON this constructor is not called
        super((value.trim().isEmpty()) ? -1 : Integer.parseInt(stripPercent(value)));
    }

    @Override
    public String toString() {
        return "IntValue{"
                + "value=" + getValue()
                + '}';
    }
}
