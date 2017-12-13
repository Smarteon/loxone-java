package cz.smarteon.loxone.message;

import cz.smarteon.loxone.Codec;

import java.nio.ByteBuffer;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.toUnsignedIntHex;
import static cz.smarteon.loxone.Codec.toUnsignedShortHex;

public class LoxoneUuid {

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

    @Override
    public String toString() {
        return toUnsignedIntHex(id1) + "-"
                + toUnsignedShortHex(id2) + "-"
                + toUnsignedShortHex(id3) + "-"
                + bytesToHex(id4);
    }
}
