package cz.smarteon.loxone;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.MessageKind;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;
import static cz.smarteon.loxone.message.MessageHeader.FIRST_BYTE;
import static cz.smarteon.loxone.message.MessageHeader.PAYLOAD_LENGTH;

public abstract class Codec {

    private static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    private static ObjectMapper MAPPER = new ObjectMapper()
            .configure(ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(ALLOW_UNQUOTED_CONTROL_CHARS, true)
            .setDateFormat(DATE_FORMAT)
            .setLocale(Locale.getDefault());

    private static final char SEPARATOR = ':';

    static String concat(String first, String second) {
        return first + SEPARATOR + second;
    }

    static byte[] concatToBytes(String first, String second) {
        return concat(first, second).getBytes();
    }

    static byte[] concat(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + 1 + b.length);
        result[a.length] = SEPARATOR;

        for (int i = 0; i < b.length; i++) {
            result[a.length + i + 1] = b[i];
        }
        return result;
    }

    /**
     * Decodes HEX represented as String in bytes to String
     *
     * @param hex String to decode
     */
    public static byte[] hexToBytes(String hex) {
        final byte[] decoded = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            decoded[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return decoded;
    }

    public static String bytesToHex(byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    public static String writeMessage(final Object message) throws IOException {
        return MAPPER.writeValueAsString(message);
    }

    public static LoxoneMessage readMessage(final String message) throws IOException {
        return readMessage(message, LoxoneMessage.class);
    }

    public static LoxoneMessage readMessage(final InputStream message) throws IOException {
        return readMessage(message, LoxoneMessage.class);
    }

    public static <T> T readMessage(final String message, final Class<T> clazz) throws IOException {
        return MAPPER.readValue(message, clazz);
    }

    public static <T> T readMessage(final InputStream message, final Class<T> clazz) throws IOException {
        return MAPPER.readValue(message, clazz);
    }

    public static MessageHeader readHeader(final ByteBuffer bytes) {
        bytes.order(ByteOrder.LITTLE_ENDIAN).rewind();
        final int limit = bytes.limit();
        final byte first = bytes.get();
        if (limit != PAYLOAD_LENGTH || first != FIRST_BYTE) {
            throw new LoxoneException("Payload is not a valid loxone message header, size=" + limit + ", firstByte=" + first);
        } else {

            return new MessageHeader(
                    MessageKind.valueOf(bytes.get()),
                    BitSet.valueOf(new byte[]{bytes.get()}).get(0),
                    readUnsingedInt(bytes, 4));
        }
    }

    public static Collection<ValueEvent> readValueEvents(final ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN).rewind();
        final List<ValueEvent> events = new ArrayList<>(buffer.limit() / ValueEvent.PAYLOAD_LENGTH);
        while (buffer.position() < buffer.limit()) {
            events.add(new ValueEvent(
                    readUuid(buffer),
                    buffer.getDouble()));
        }
        return events;
    }

    public static Collection<TextEvent> readTextEvents(final ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN).rewind();
        final List<TextEvent> events = new ArrayList<>();
        while (buffer.position() < buffer.limit()) {
            events.add(new TextEvent(
                    readUuid(buffer),
                    readUuid(buffer),
                    new String(readBytes(buffer, Long.valueOf(readUnsingedInt(buffer)).intValue()))));
            buffer.position(Math.min(buffer.limit(), buffer.position() + 4 - (buffer.position() % 4))); // text events padded to multiple of 4
        }
        return events;
    }

    public static String toUnsignedIntHex(final long value) {
        return bytesToHex(toUnsignedIntBytes(value));
    }

    public static String toUnsignedShortHex(final int value) {
        return bytesToHex(toUnsignedShortBytes(value));
    }

    private static byte[] toUnsignedShortBytes(final int value) {
        return ByteBuffer.allocate(2).putShort((short) (value & 0xffff)).array();
    }

    private static byte[] toUnsignedIntBytes(final long value) {
        return ByteBuffer.allocate(4).putInt((int) (value & 0xffffffffL)).array();
    }

    private static LoxoneUuid readUuid(final ByteBuffer buffer) {
        return new LoxoneUuid(
                readUnsingedInt(buffer),
                readUnsignedShort(buffer),
                readUnsignedShort(buffer),
                readBytes(buffer, 8));

    }

    static byte[] readBytes(final ByteBuffer buffer) {
        return readBytes(buffer, buffer.remaining());
    }

    private static byte[] readBytes(final ByteBuffer buffer, final int length) {
        // sanitize when less than declared length actually received
        int l = length >= buffer.remaining() ? buffer.remaining() : length;
        final byte[] bytes = new byte[l];
        buffer.get(bytes, 0, l);
        return bytes;
    }

    static int readUnsignedShort(final ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    static long readUnsingedInt(final ByteBuffer buffer) {
        return readUnsingedInt(buffer, -1);
    }

    private static long readUnsingedInt(final ByteBuffer buffer, final int position) {
        if (position >= 0) {
            buffer.position(position);
        }
        return buffer.getInt() & 0xffffffffL;
    }
}
