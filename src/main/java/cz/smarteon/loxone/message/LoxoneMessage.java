package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Represents single message of loxone API.
 * @param <V> type of value.
 */
public class LoxoneMessage<V extends LoxoneValue> {

    public static final int CODE_OK = 200;
    public static final int CODE_AUTH_FAIL = 401;
    public static final int CODE_NOT_AUTHENTICATED = 400;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_AUTH_TOO_LONG = 420;
    public static final int CODE_UNAUTHORIZED = 500;

    @JsonProperty("LL")
    protected final Content<V> content;

    public LoxoneMessage(final @NotNull String control, final @NotNull Integer code, final @NotNull V value) {
        this.content = new Content<>(control, code, value);
    }

    @JsonCreator
    LoxoneMessage(@JsonProperty("LL") final Content<V> content) {
        this.content = requireNonNull(content, "content can't be null");
    }

    /**
     * Control identifier, the message is about / from
     * @return identifier of the control
     */
    @NotNull @JsonIgnore
    public String getControl() {
        return content.getControl();
    }

    /**
     * Return code
     * @return return code
     */
    @JsonIgnore
    public int getCode() {
        return content.getCode();
    }

    /**
     * Value of the message.
     * @return message value
     */
    @NotNull @JsonIgnore
    public V getValue() {
        return content.getValue();
    }

    /**
     * Evaluates if this message is successful response.
     * @return true if {@link #getCode()} returns 200, false otherwise
     */
    @JsonIgnore
    public boolean isSuccess() {
        return getCode() == CODE_OK;
    }

    /**
     * Evaluates if this messages represents authentication failed response.
     * @return true if {@link #getCode()} is 400, 401 or 420
     */
    @JsonIgnore
    public boolean isAuthFailed() {
        return getCode() == CODE_AUTH_FAIL || getCode() == CODE_NOT_AUTHENTICATED || getCode() == CODE_AUTH_TOO_LONG;
    }

    @Override
    public String toString() {
        return "LoxoneMessage{" +
                "control='" + getControl() + '\'' +
                ", code=" + getCode() +
                ", value=" + getValue() +
                '}';
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "code", defaultImpl = ErrorContent.class, visible = true)
    @JsonSubTypes({@JsonSubTypes.Type(name = "200", value = Content.class)})
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Content<V extends LoxoneValue> {

        protected final String control;
        protected final Integer code;
        protected final V value;

        @JsonCreator
        Content(
                @JsonProperty("control") final String control,
                @JsonProperty("code") @JsonDeserialize(using = LoxoneIntDeserializer.class) final Integer code,
                @JsonProperty("value")
                @JsonTypeInfo(
                        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                        property = "control",
                        use = JsonTypeInfo.Id.CUSTOM,
                        defaultImpl = JsonValue.class)
                @JsonTypeIdResolver(LoxoneValueTypeResolver.class) final V value) {
            this.control = requireNonNull(control, "control can't be null");
            this.code = requireNonNull(code, "code can't be null");
            this.value = requireNonNull(value, "value can't be null");
        }

        public String getControl() {
            return control;
        }

        public Integer getCode() {
            return code;
        }

        public V getValue() {
            return value;
        }
    }

    protected static class ErrorContent extends Content<JsonValue> {
        @JsonCreator
        ErrorContent(
                @JsonProperty("control") final String control,
                @JsonProperty("code") @JsonDeserialize(using = LoxoneIntDeserializer.class) final Integer code,
                @JsonProperty("value") final JsonValue value) {
            super(control, code, value);
        }
    }
}
