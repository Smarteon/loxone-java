package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

/**
 * {@link LoxoneValue} where the JSON string is expected to return an empty value.
 */
@Getter
@JsonDeserialize(using = EmptyValue.EmptyValueDeserializer.class)
public class EmptyValue implements LoxoneValue {

    private static EmptyValue INSTANCE;
    private final String empty;

    private EmptyValue() {
        this.empty = "";
    }

    public static EmptyValue getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new EmptyValue();
        }
        return INSTANCE;
    }

    public static class EmptyValueDeserializer extends JsonDeserializer<EmptyValue> {
        @Override
        public EmptyValue deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return getInstance();
        }
    }
}
