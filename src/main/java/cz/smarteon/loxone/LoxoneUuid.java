package cz.smarteon.loxone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;

import java.nio.ByteBuffer;
import java.util.Objects;

import static cz.smarteon.loxone.Codec.*;

@EqualsAndHashCode
public final class LoxoneUuid {

    private final long id1;
    private final int id2;
    private final int id3;
    private final byte[] id4;

    public LoxoneUuid(long id1, int id2, int id3, byte[] id4) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.id4 = id4;
    }

    @JsonCreator
    public LoxoneUuid(String value) {
        final String[] parts = Objects.requireNonNull(value).split("-");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Unparseable uuid " + value);
        } else {
            id1 = readUnsingedInt(ByteBuffer.wrap(hexToBytes(parts[0])));
            id2 = readUnsignedShort(ByteBuffer.wrap(hexToBytes(parts[1])));
            id3 = readUnsignedShort(ByteBuffer.wrap(hexToBytes(parts[2])));
            id4 = hexToBytes(parts[3]);
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return toUnsignedIntHex(id1) + "-"
                + toUnsignedShortHex(id2) + "-"
                + toUnsignedShortHex(id3) + "-"
                + bytesToHex(id4);
    }

}
