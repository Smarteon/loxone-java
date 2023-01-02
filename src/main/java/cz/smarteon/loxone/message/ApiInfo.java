package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents miniserver's basic info.
 */
@JsonDeserialize(using = ApiInfo.Deserializer.class)
public class ApiInfo implements LoxoneValue {

    /* MAC Address of the Miniserver */
    private final String mac;

    /* Loxone Config Version */
    private final String version;
    private Boolean eventSlots;

    public ApiInfo(final String mac, final String version) {
        this.mac = mac;
        this.version = version;
    }

    public ApiInfo(final String mac, final String version, final Boolean eventSlots) {
        this.mac = mac;
        this.version = version;
        this.eventSlots = eventSlots;
    }

    public String getMac() {
        return mac;
    }

    public String getVersion() {
        return version;
    }

    @Nullable
    public Boolean getEventSlots() {
        return eventSlots;
    }

    @JsonValue
    private String jsonValue() {
        return "{'snr':'" + mac + "', 'version':'" + version + "'}";
    }

    /**
     * Used to correctly deserialize {@link ApiInfo}.
     */
    public static class Deserializer extends JsonDeserializer<ApiInfo> {

        // the \\} is reported as redundant escape by Intellij, however android can't parse the regex without it :-(
        // the regex is used because the content of ApiInfo is not real JSON as it uses ' char instead of ".
        private static final Pattern API_VALUE_PATTERN = Pattern
                .compile("\\{\\s*'snr'\\s*:\\s*'([^']+)'\\s*,\\s*'version'"
                        + "\\s*:\\s*'([^']+)'\\s*,?\\s*(?:'hasEventSlots')?\\s*:?\\s*(.*)\\}");

        @Override
        public ApiInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String apiValue = p.readValueAs(String.class);
            final Matcher matcher = API_VALUE_PATTERN.matcher(apiValue);
            if (matcher.find()) {
                if (matcher.groupCount() > 2) {
                    return new ApiInfo(matcher.group(1), matcher.group(2), "true".equals(matcher.group(3)));
                }
                return new ApiInfo(matcher.group(1), matcher.group(2));
            } else {
                throw JsonMappingException.from(p, "Can't parse api info using regex");
            }
        }
    }
}
