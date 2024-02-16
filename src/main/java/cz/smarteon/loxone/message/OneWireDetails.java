package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
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
public final class OneWireDetails implements LoxoneValue {

    private final Map<String, OneWireDetail> details;
    private final String invalid;


    private OneWireDetails(final Map<String, OneWireDetail> details) {
        this.details = requireNonNull(details, "details can't be null");
        this.invalid = null;
    }

    private OneWireDetails(final String invalid) {
        this.details = Collections.emptyMap();
        this.invalid = invalid;
    }

    /**
     * Map of 1-wire details, where key is the serial number and value represents single device detail.
     * @return map of 1-wire details
     */
    @NotNull
    public Map<String, OneWireDetail> asMap() {
        return details;
    }

    /**
     * The original string received from miniserver but not representing valid OneWireDetails.
     * @return original invalid string
     */
    @Nullable
    public String getInvalid() {
        return invalid;
    }

    /**
     * Signals the invalid representation has been received from miniserver.
     * @return true if invalid false otherwise
     */
    public boolean isInvalid() {
        return invalid != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OneWireDetails that = (OneWireDetails) o;
        return Objects.equals(details, that.details)
                && Objects.equals(invalid, that.invalid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details, invalid);
    }

    /**
     * Represents OneWire device.
     */
    @SuppressWarnings({"checkstyle:membername", "checkstyle:methodname"})
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
         * 1-wire device serial number.
         * @return serial number
         */
        @NotNull
        public String getSerial() {
            return serial;
        }

        /**
         * Count of packet requests since last extension restart.
         * @return count of packet request
         */
        public long getPacketRequests() {
            return packetRequests;
        }

        /**
         * Count of CRC errors since last extension restart.
         * @return count of CRC errors
         */
        public long getCrcErrors() {
            return crcErrors;
        }

        /**
         * Count of so called 85 degree errors since last extension restart.
         * @return count of 85 degree errors
         */
        public int get_85DegreeErrors() {
            return _85DegreeErrors;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final OneWireDetail that = (OneWireDetail) o;
            return packetRequests == that.packetRequests
                    && crcErrors == that.crcErrors
                    && _85DegreeErrors == that._85DegreeErrors
                    && Objects.equals(serial, that.serial);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serial, packetRequests, crcErrors, _85DegreeErrors);
        }
    }

    /**
     * Used to correctly deserialize {@link OneWireDetails}.
     */
    public static class Deserializer extends JsonDeserializer<OneWireDetails> {

        private static final Pattern ONE_WIRE_DETAIL_PATTERN = Pattern.compile(
                "^(?:1-Wire\\s+)?Serial\\s+([0-9A-F.]+):?\\s+(\\d+)\\s+Packet(?: Request)?s,"
                        + "\\s+(\\d+)\\s+CRC(?: Errors)?,\\s+(\\d+)\\s+85 (?:Degree Problems|err)$");
        @Override
        public OneWireDetails deserialize(
                final JsonParser p,
                final DeserializationContext ctxt) throws IOException {
            final String textValue = p.getValueAsString();
            final String[] devices = textValue.trim().split("\\s*;\\s*");
            final Map<String, OneWireDetail> detailsMap = stream(devices)
                    .map(String::trim)
                    .map(ONE_WIRE_DETAIL_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> new OneWireDetail(matcher.group(1),
                            Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)))
                    )
                    .collect(Collectors.toMap(OneWireDetail::getSerial, Function.identity()));

            if (devices.length == detailsMap.size()) {
                return new OneWireDetails(detailsMap);
            } else {
                return new OneWireDetails(textValue);
            }
        }
    }
}
