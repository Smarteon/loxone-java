package cz.smarteon.loxone.message;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Represents command for loxone control item.
 * @param <V> value type
 */
public class ControlCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String CONTROL_PREFIX = "jdev/sps/io";

    ControlCommand(final String controlUuid, final String operation, final Class<V> valueType) {
        super(requireNonNull(controlUuid, "controlUuid can't be null") +
                "/" + requireNonNull(operation, "operation can't be null"), Type.JSON, valueType, true, true);
    }

    /**
     * Creates generic control command resulting in {@link JsonValue}.
     * @param uuid control identifier
     * @param operation operation
     * @return control command
     */
    @NotNull
    public static ControlCommand<JsonValue> genericControlCommand(final @NotNull String uuid, final @NotNull String operation) {
        return new ControlCommand<>(uuid, operation, JsonValue.class);
    }

    @NotNull
    public String getControlCommand() {
        return super.getCommand();
    }

    @Override
    public String getCommand() {
        return CONTROL_PREFIX + "/" + super.getCommand();
    }
}
