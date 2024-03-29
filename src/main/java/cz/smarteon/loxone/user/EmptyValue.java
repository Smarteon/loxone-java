package cz.smarteon.loxone.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.Getter;

import java.io.IOException;

/**
 * {@link LoxoneValue} where the JSON string is expected to return an empty value.
 */
@Getter
@JsonDeserialize(using = EmptyValue.EmptyValueDeserializer.class)
public final class EmptyValue implements LoxoneValue {

    private static EmptyValue instance;
    private final String empty;

    private EmptyValue() {
        this.empty = "";
    }

    public static EmptyValue getInstance() {
        if (instance == null) {
            instance = new EmptyValue();
        }
        return instance;
    }

    /**
     * Used to properly deserialize {@link EmptyValue}.
     */
    public static class EmptyValueDeserializer extends JsonDeserializer<EmptyValue> {
        @Override
        public EmptyValue deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return getInstance();
        }
    }
}
