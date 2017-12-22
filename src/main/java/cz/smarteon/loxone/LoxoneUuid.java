package cz.smarteon.loxone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.smarteon.loxone.config.ControlState;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.hexToBytes;
import static cz.smarteon.loxone.Codec.readUnsignedShort;
import static cz.smarteon.loxone.Codec.readUnsingedInt;
import static cz.smarteon.loxone.Codec.toUnsignedIntHex;
import static cz.smarteon.loxone.Codec.toUnsignedShortHex;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoxoneUuid that = (LoxoneUuid) o;

        if (id1 != that.id1) return false;
        if (id2 != that.id2) return false;
        if (id3 != that.id3) return false;
        return Arrays.equals(id4, that.id4);
    }

    @Override
    public int hashCode() {
        int result = (int) (id1 ^ (id1 >>> 32));
        result = 31 * result + id2;
        result = 31 * result + id3;
        result = 31 * result + Arrays.hashCode(id4);
        return result;
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
