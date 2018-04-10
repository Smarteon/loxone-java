package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class LoxoneIntDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.readValueAsTree();
        if (node.isNull()) {
            return null;
        } else if (node.isInt()) {
            return node.intValue();
        } else if (node.isTextual()) {
            try {
                return Integer.valueOf(node.textValue());
            } catch (NumberFormatException e) {
                throw JsonMappingException.from(p, "Invalid integer representation", e);
            }
        } else {
            throw JsonMappingException.from(p, "Unknown type of integer value " + node.getNodeType());
        }
    }
}
