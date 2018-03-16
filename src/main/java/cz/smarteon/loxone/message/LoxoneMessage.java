package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeName("LL")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class LoxoneMessage {
    protected final String control;
    protected final Integer code;
    protected final LoxoneValue value;

    @JsonCreator
    public LoxoneMessage(@JsonProperty("control") final String control,
                            @JsonProperty("code") @JsonDeserialize(using = LoxoneIntDeserializer.class) final Integer code,
                            @JsonProperty("value")
                            @JsonTypeInfo(
                                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                                    property = "control",
                                    use = JsonTypeInfo.Id.CUSTOM,
                                    defaultImpl = JsonValue.class)
                            @JsonTypeIdResolver(LoxoneValueTypeResolver.class) final LoxoneValue value) {
        this.control = control;
        this.code = code;
        this.value = value;
    }

    public String getControl() {
        return control;
    }

    public int getCode() {
        return code;
    }

    public LoxoneValue getValue() {
        return value;
    }

    @JsonAnySetter
    public void add(final String key, final JsonNode value) {
        // hack causing to ignore unknown fields since @JsonIgnoreProperties(ignoreUnknown = true) doesn't work together custom type resolver
    }
}
