package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents miniserver's heap info.
 */
@JsonDeserialize(using = Heap.Deserializer.class)
public class Heap implements LoxoneValue {
    private final int used;
    private final int allowed;

    public Heap(final int used, final int allowed) {
        this.used = used;
        this.allowed = allowed;
    }

    /**
     * Used kB of heap.
     * @return used heap
     */
    public int getUsed() {
        return used;
    }

    /**
     * Allowed kB of heap.
     * @return allowed heap size
     */
    public int getAllowed() {
        return allowed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Heap heap = (Heap) o;
        return used == heap.used
                && allowed == heap.allowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(used, allowed);
    }

    @Override
    public String toString() {
        return "Heap{"
                + "used=" + used
                + ", allowed=" + allowed
                + '}';
    }

    /**
     * Used to correctly deserialize {@link Heap}.
     */
    public static class Deserializer extends JsonDeserializer<Heap> {

        private static final Pattern HEAP_VALUE_PATTERN = Pattern.compile("([0-9]+)/([0-9]+)kB");

        @Override
        public Heap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String heapValue = p.readValueAs(String.class);
            final Matcher matcher = HEAP_VALUE_PATTERN.matcher(heapValue);
            if (matcher.find()) {
                return new Heap(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            } else {
                throw JsonMappingException.from(p, "Can't parse heap value using regex");
            }
        }
    }
}
