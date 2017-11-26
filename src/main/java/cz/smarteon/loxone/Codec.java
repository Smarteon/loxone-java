package cz.smarteon.loxone;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.smarteon.loxone.message.LoxoneMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Formatter;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;

public abstract class Codec {

    private static ObjectMapper MAPPER = new ObjectMapper()
            .configure(ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(ALLOW_UNQUOTED_CONTROL_CHARS, true);

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

        for (int i=0; i<b.length;i++) {
            result[a.length + i + 1] = b[i];
        }
        return result;
    }

    /**
     * Decodes HEX represented as String in bytes to String
     * @param hex String to decode
     * */
    public static byte[] hexToBytes(String hex) {
        final byte[] decoded = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i+=2) {
            decoded[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
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

    public static LoxoneMessage readMessage(final String message) throws IOException {
        return MAPPER.readValue(message, LoxoneMessage.class);
    }

    public static LoxoneMessage readMessage(final InputStream message) throws IOException {
        return MAPPER.readValue(message, LoxoneMessage.class);
    }
}
