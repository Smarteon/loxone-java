package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NfcTag {

    /**
     * Id of this NFC tag.
     */
    private final @NotNull String id;

    /**
     * Name if this NFC tag.
     */
    private final @Nullable String name;

    @JsonCreator
    public NfcTag (
            @JsonProperty(value = "name") @NotNull String name,
            @JsonProperty(value = "id", required = true) @NotNull String id) {
        this.name = requireNonNull(name, "name can't be null");
        this.id = requireNonNull(id, "id can't be null");
    }

    public NfcTag (@NotNull String id) {
        this.id = requireNonNull(id, "id can't be null");
        this.name = null;
    }

    public static class NfcTagSerializer extends StdSerializer<NfcTag> {

        public NfcTagSerializer() {
            this(null);
        }

        public NfcTagSerializer(Class<NfcTag> t) {
            super(t);
        }

        @Override
        public void serialize(NfcTag value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.getId());
        }
    }
}
