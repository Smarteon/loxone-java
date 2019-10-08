package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Represents single message of loxone API.
 * @param <V> type of value.
 */
@JsonTypeName("LL")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class LoxoneMessage<V extends LoxoneValue> {
    protected final String control;
    protected final Integer code;
    protected final V value;

    @JsonCreator
    public LoxoneMessage(@JsonProperty("control") final String control,
                            @JsonProperty("code") @JsonDeserialize(using = LoxoneIntDeserializer.class) final Integer code,
                            @JsonProperty("value")
                            @JsonTypeInfo(
                                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                                    property = "control",
                                    use = JsonTypeInfo.Id.CUSTOM,
                                    defaultImpl = JsonValue.class)
                            @JsonTypeIdResolver(LoxoneValueTypeResolver.class) final V value) {
        this.control = control;
        this.code = code;
        this.value = value;
    }

    /**
     * Control identifier, the message is about / from
     * @return identifier of the control
     */
    public String getControl() {
        return control;
    }

    /**
     * Return code
     * @return return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Value of the message.
     * @return message value
     */
    public V getValue() {
        return value;
    }

    @JsonAnySetter
    public void add(final String key, final JsonNode value) {
        // hack causing to ignore unknown fields since @JsonIgnoreProperties(ignoreUnknown = true) doesn't work together custom type resolver
    }
}
