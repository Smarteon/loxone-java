package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode long value.
 */
public class LongValue implements LoxoneValue {

    private final long value;

    @JsonCreator
    public LongValue(final String value) {
        this.value = Long.parseLong(value);
    }

    @JsonIgnore
    public long getValue() {
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
        final LongValue longValue = (LongValue) o;
        return value == longValue.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "LongValue{"
                + "value=" + value
                + '}';
    }
}
