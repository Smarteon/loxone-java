package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.LoxoneException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Default type of {@link LoxoneValue} used in cases when {@link LoxoneMessage#getControl()} can't be used to determine
 * more specific type. It's basically wrapper for {@link JsonNode}.
 */
@JsonSerialize(using = JsonValue.Serializer.class)
public class JsonValue implements LoxoneValue {

    private final JsonNode jsonNode;

    @JsonCreator
    public JsonValue(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @JsonIgnore
    public JsonNode getJsonNode() {
        return jsonNode;
    }

    /**
     * Tries to convert this value to more specific {@link LoxoneValue}.
     * @throws LoxoneException in case of conversion failure.
     * @see Codec#convertValue(JsonNode, Class)
     * @param type type to convert to
     * @param <V> returned type
     * @return converted value
     */
    @NotNull
    public <V extends LoxoneValue> V as(final @NotNull Class<V> type) {
        try {
            return Codec.convertValue(jsonNode, type);
        } catch (IllegalArgumentException e) {
            throw new LoxoneException("Can't convert value to type " + type, e);
        }
    }

    /**
     * Used to correctly serialize {@link JsonValue}.
     */
    public static class Serializer extends JsonSerializer<JsonValue> {
        @Override
        public void serialize(
                final JsonValue value,
                final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException {
            gen.writeRawValue(value.jsonNode.toString());
        }
    }
}
