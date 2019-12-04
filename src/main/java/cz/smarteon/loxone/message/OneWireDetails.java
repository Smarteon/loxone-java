package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

/**
 * {@link LoxoneValue} where the JSON string is expected to contain 1-Wire devices details.
 * @see LoxoneMessageCommand#oneWireDetails(String)
 */
@JsonDeserialize(using = OneWireDetails.Deserializer.class)
public class OneWireDetails implements LoxoneValue {

    private final Map<String, OneWireDetail> details;

    private OneWireDetails(final Map<String, OneWireDetail> details) {
        this.details = requireNonNull(details, "details can't be null");
    }

    /**
     * Map of 1-ire details, where key is the serial number and value represents single device detail.
     * @return map of 1-wire details
     */
    @NotNull
    public Map<String, OneWireDetail> asMap() {
        return details;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneWireDetails that = (OneWireDetails) o;
        return Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details);
    }

    public static class OneWireDetail {
        private final String serial;
        private final long packetRequests;
        private final long crcErrors;
        private final int _85DegreeErrors;

        OneWireDetail(final String serial, final long packetRequests, final long crcErrors, final int degreeErrors) {
            this.serial = requireNonNull(serial, "serial can't be null");
            this.packetRequests = packetRequests;
            this.crcErrors = crcErrors;
            _85DegreeErrors = degreeErrors;
        }

        /**
         * 1-wire device serial number
         * @return serial number
         */
        @NotNull
        public String getSerial() {
            return serial;
        }

        /**
         * Count of packet requests since last extension restart
         * @return count of packet request
         */
        public long getPacketRequests() {
            return packetRequests;
        }

        /**
         * Count of CRC errors since last extension restart
         * @return count of CRC errors
         */
        public long getCrcErrors() {
            return crcErrors;
        }

        /**
         * Count of so called 85 degree errors since last extension restart
         * @return count of 85 degree errors
         */
        public int get_85DegreeErrors() {
            return _85DegreeErrors;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OneWireDetail that = (OneWireDetail) o;
            return packetRequests == that.packetRequests &&
                    crcErrors == that.crcErrors &&
                    _85DegreeErrors == that._85DegreeErrors &&
                    Objects.equals(serial, that.serial);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serial, packetRequests, crcErrors, _85DegreeErrors);
        }
    }

    public static class Deserializer extends JsonDeserializer<OneWireDetails> {

        private static final Pattern ONE_WIRE_DETAIL_PATTERN = Pattern.compile(
                "^1-Wire\\s+Serial\\s+([0-9A-F.]+):\\s+(\\d+)\\s+Packet Requests,\\s+(\\d+)\\s+CRC Errors,\\s+(\\d+)\\s+85 Degree Problems$");
        @Override
        public OneWireDetails deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final String[] devices = p.getValueAsString().trim().split("\\s*;\\s*");
            final OneWireDetails oneWireDetails = new OneWireDetails(
                    stream(devices)
                            .map(String::trim)
                            .map(ONE_WIRE_DETAIL_PATTERN::matcher)
                            .filter(Matcher::find)
                            .map(matcher -> new OneWireDetail(matcher.group(1),
                                    Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)),
                                    Integer.parseInt(matcher.group(4)))
                            )
                            .collect(Collectors.toMap(OneWireDetail::getSerial, Function.identity())));
            if (devices.length != oneWireDetails.asMap().size()) {
                throw JsonMappingException.from(p, "Error while parsing one-wire detail using regex");
            }
            return oneWireDetails;
        }
    }
}
