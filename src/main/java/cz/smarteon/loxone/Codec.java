package cz.smarteon.loxone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.MessageHeader;
import cz.smarteon.loxone.message.MessageKind;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;
import static cz.smarteon.loxone.message.MessageHeader.FIRST_BYTE;
import static cz.smarteon.loxone.message.MessageHeader.PAYLOAD_LENGTH;

/**
 * Coding and decoding utilities. Central point in the library for all the coding and decoding configuration and logic.
 */
@SuppressWarnings({"InnerTypeLast", "ClassDataAbstractionCoupling"})
public abstract class Codec {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final char SEPARATOR = ':';

    private static final Function<byte[], String> DEFAULT_BASE64_ENCODER =
            bytes -> Base64.getEncoder().encodeToString(bytes);
    private static final Function<String, byte[]> DEFAULT_BASE64_DECODER =
            encoded -> Base64.getDecoder().decode(encoded);

    private static Function<byte[], String> base64encoder = DEFAULT_BASE64_ENCODER;
    private static Function<String, byte[]> base64decoder = DEFAULT_BASE64_DECODER;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    private static class JsonMapperHolder {
        static final ObjectMapper MAPPER = JsonMapper.builder()
                .configure(ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .enable(ALLOW_UNESCAPED_CONTROL_CHARS)
                .defaultDateFormat(DATE_FORMAT)
                .defaultLocale(Locale.getDefault())
                .build();
    }

    static String concat(String first, String second) {
        return first + SEPARATOR + second;
    }

    static byte[] concatToBytes(String first, String second) {
        return concat(first, second).getBytes();
    }

    /**
     * Decodes HEX represented as String in bytes to String.
     *
     * @param hex String to decode
     * @return the decoded bytes
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

        final Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    /**
     * Encodes given bytes to Base64 and returns it as IS0-8859-1 encoded String.
     * @param bytes bytes to encode
     * @return encoded bytes
     */
    @NotNull
    public static String bytesToBase64(final byte[] bytes) {
        return base64encoder.apply(bytes);
    }

    /**
     * Decodes given Base64 encoded String to bytes.
     * @param base64 to decode
     * @return decoded String
     * @throws IllegalArgumentException in case the input is not correct Base64
     */
    public static byte[] base64ToBytes(final @NotNull String base64) {
        return base64decoder.apply(base64);
    }

    /**
     * Allows setting different Base64 encoder and decoder than the default ({@link java.util.Base64}). The codec is
     * then used in {@link #base64ToBytes(String)} and {@link #bytesToBase64(byte[])} functions.
     * @param encoder encoder to use
     * @param decoder decoder to use
     * @see #resetBase64Codec()
     */
    public static void setBase64Codec(
            final @NotNull Function<byte[], String> encoder,
            final @NotNull Function<String, byte[]> decoder) {
        base64encoder = encoder;
        base64decoder = decoder;
    }

    /**
     * Resets the Base64 encoder and decoder to default ({@link java.util.Base64}).
     * @see #setBase64Codec(Function, Function)
     */
    public static void resetBase64Codec() {
        setBase64Codec(DEFAULT_BASE64_ENCODER, DEFAULT_BASE64_DECODER);
    }

    public static String writeMessage(final Object message) throws IOException {
        return JsonMapperHolder.MAPPER.writeValueAsString(message);
    }

    @Deprecated
    public static LoxoneMessage<?> readMessage(final String message) throws IOException {
        return readMessage(message, LoxoneMessage.class);
    }

    @Deprecated
    public static LoxoneMessage<?> readMessage(final InputStream message) throws IOException {
        return readMessage(message, LoxoneMessage.class);
    }

    public static <T> T readMessage(final String message, final Class<T> clazz) throws IOException {
        return JsonMapperHolder.MAPPER.readValue(message, clazz);
    }

    public static <T> T readMessage(final InputStream message, final Class<T> clazz) throws IOException {
        return JsonMapperHolder.MAPPER.readValue(message, clazz);
    }

    public static <T> T readXml(final InputStream xml, final Class<T> clazz) throws IOException {
        try {
            final JAXBContext ctx = JAXBContext.newInstance(clazz);
            return clazz.cast(ctx.createUnmarshaller().unmarshal(xml));
        } catch (JAXBException e) {
            throw new IOException("Can't unmarshall XML", e);
        }
    }

    /**
     * Converts the given json node to the type of given class.
     * @throws IllegalArgumentException in case the node is not convertible.
     * @param jsonNode to convert
     * @param clazz type class
     * @param <T> type
     * @return converted object
     */
    public static <T> T convertValue(final @NotNull JsonNode jsonNode, final @NotNull Class<T> clazz) {
        return JsonMapperHolder.MAPPER.convertValue(jsonNode, clazz);
    }

    public static MessageHeader readHeader(final ByteBuffer bytes) {
        bytes.order(ByteOrder.LITTLE_ENDIAN).rewind();
        final int limit = bytes.limit();
        final byte first = bytes.get();
        if (limit != PAYLOAD_LENGTH || first != FIRST_BYTE) {
            throw new LoxoneException("Payload is not a valid loxone message header, size="
                    + limit + ", firstByte=" + first);
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

            // text events padded to multiple of 4
            final int padding = buffer.position() % 4;
            if (padding > 0) {
                buffer.position(Math.min(buffer.limit(), buffer.position() + 4 - padding));
            }
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

    private static byte[] readBytes(final ByteBuffer buffer, final int length) {
        // sanitize when less than declared length actually received
        final int l = Math.min(length, buffer.remaining());
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
