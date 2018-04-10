package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cz.smarteon.loxone.Codec;

import java.io.IOException;

class HexDeserializer extends JsonDeserializer<byte[]> {
    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Codec.hexToBytes(p.getValueAsString());
    }
}
