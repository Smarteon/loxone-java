package cz.smarteon.loxone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public class PercentDoubleDeserializer extends JsonDeserializer<Double> {
    @Override
    public Double deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final String stringVal = p.getValueAsString();
        try {
            return Double.valueOf(stripPercent(stringVal));
        } catch (Exception e) {
            throw JsonMappingException.from(p, "Unable to deserialize percentage from" + stringVal, e);
        }
    }

    public static String stripPercent(final String toStrip) {
        if (toStrip != null && toStrip.endsWith("%")) {
            return toStrip.substring(0, toStrip.length()-1);
        } else {
            throw new IllegalArgumentException("Invalid percentage value: " + toStrip);
        }
    }
}
