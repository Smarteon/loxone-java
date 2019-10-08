package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;

import static cz.smarteon.loxone.PercentDoubleDeserializer.stripPercent;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode integer percentage.
 */
public class PercentValue extends IntValue {

    @JsonCreator
    PercentValue(final String value) {
        super(stripPercent(value));
    }

    @Override
    public String toString() {
        return "IntValue{" +
                "value=" + getValue() +
                '}';
    }
}
