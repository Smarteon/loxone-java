package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.smarteon.loxone.Codec;

import java.io.IOException;

class HexSerializer extends JsonSerializer<byte[]> {
    @Override
    public void serialize(
            final byte[] value,
            final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException {
        gen.writeString(Codec.bytesToHex(value));
    }
}
