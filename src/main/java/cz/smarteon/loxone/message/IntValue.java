package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode integer value.
 */
public class IntValue implements LoxoneValue {

    private final int value;

    @JsonCreator
    IntValue(final String value) {
        this.value = Integer.parseInt(value);
    }

    public IntValue(final int value) {
        this.value = value;
    }

    @JsonIgnore
    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IntValue longValue = (IntValue) o;
        return value == longValue.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntValue{"
                + "value=" + value
                + '}';
    }
}
