package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonDeserialize(using = ApiInfo.Deserializer.class)
public class ApiInfo implements LoxoneValue {

    private final String mac;
    private final String version;

    ApiInfo(final String mac, final String version) {
        this.mac = mac;
        this.version = version;
    }

    public String getMac() {
        return mac;
    }

    public String getVersion() {
        return version;
    }

    public static class Deserializer extends JsonDeserializer<ApiInfo> {

        // the \\} is reported as redundant escape by Intellij, however android can't parse the regex without it :-(
        private static final Pattern API_VALUE_PATTERN = Pattern.compile("\\{\\s*'snr'\\s*:\\s*'([^']+)'\\s*,\\s*'version'\\s*:\\s*'([^']+)'\\s*\\}");

        @Override
        public ApiInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String apiValue = p.readValueAs(String.class);
            final Matcher matcher = API_VALUE_PATTERN.matcher(apiValue);
            if (matcher.find()) {
                return new ApiInfo(matcher.group(1), matcher.group(2));
            } else {
                throw JsonMappingException.from(p, "Can't parse api info using regex");
            }
        }
    }
}
