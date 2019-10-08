package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode integer percentage.
 */
public class PercentValue extends IntValue {

    @JsonCreator
    PercentValue(final String value) {
        super(stripPercent(value));
    }

    private static String stripPercent(final String toStrip) {
        if (toStrip != null && toStrip.endsWith("%")) {
            return toStrip.substring(0, toStrip.length()-1);
        } else {
            throw new IllegalArgumentException("Invalid percentage value: " + toStrip);
        }
    }

    @Override
    public String toString() {
        return "IntValue{" +
                "value=" + getValue() +
                '}';
    }
}
