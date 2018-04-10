package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

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

    public static class Serializer extends JsonSerializer<JsonValue> {
        @Override
        public void serialize(final JsonValue value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeRawValue(value.jsonNode.toString());
        }
    }
}
