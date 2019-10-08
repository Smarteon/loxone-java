package cz.smarteon.loxone.message;

import cz.smarteon.loxone.Command;

/**
 * Specific command which results into {@link LoxoneMessage} response.
 * @param <V> type of the {@link LoxoneMessage} value
 */
public class LoxoneMessageCommand<V extends LoxoneValue> extends Command<LoxoneMessage<V>> {

    /**
     * Basic information about API. Unauthenticated.
     */
    public static final LoxoneMessageCommand<ApiInfo> DEV_CFG_API = jsonHttpCommand("jdev/cfg/api", ApiInfo.class);

    /**
     * API's public key.
     */
    public static final LoxoneMessageCommand<PubKeyInfo> DEV_SYS_GETPUBLICKEY = jsonHttpCommand("jdev/sys/getPublicKey", PubKeyInfo.class);

    private final Class<V> valueType;

    @SuppressWarnings("unchecked")
    private LoxoneMessageCommand(final String command, final Type type, final Class<V> valueType, final boolean httpSupported,
                                final boolean wsSupported) {
        super(command, type, (Class<LoxoneMessage<V>>) (Class<?>) LoxoneMessage.class, httpSupported, wsSupported);
        this.valueType = valueType;
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonHttpCommand(final String command, final Class<V> valueType) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, true, false);
    }

    /**
     * Type of the {@link LoxoneMessage} value
     * @return value type
     */
    public Class<V> getValueType() {
        return valueType;
    }
}
