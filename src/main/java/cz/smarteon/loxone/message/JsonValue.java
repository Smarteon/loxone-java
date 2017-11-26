package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonValue implements LoxoneValue {

    private final JsonNode jsonNode;

    @JsonCreator
    public JsonValue(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }
}
