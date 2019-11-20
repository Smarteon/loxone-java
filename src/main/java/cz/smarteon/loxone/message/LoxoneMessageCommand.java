package cz.smarteon.loxone.message;

import cz.smarteon.loxone.Command;
import cz.smarteon.loxone.LoxoneException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

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

    /**
     * Number of system tasks.
     */
    public static final LoxoneMessageCommand<IntValue> DEV_SYS_NUMTASKS = jsonHttpCommand("jdev/sys/numtasks", IntValue.class);

    /**
     * Number of system interruptions.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_INTS = jsonHttpCommand("jdev/sys/ints", LongValue.class);

    /**
     * Number of system communication interruptions.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_COMINTS = jsonHttpCommand("jdev/sys/comints", LongValue.class);

    /**
     * Number of system LAN interruptions.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_LANINTS = jsonHttpCommand("jdev/sys/lanints", LongValue.class);

    /**
     * Number of system context switches.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_CONTEXTSWITCHES = jsonHttpCommand("jdev/sys/contextswitches", LongValue.class);

    /**
     * Number of system context switches of interrupted tasks.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_CONTEXTSWITCHESI = jsonHttpCommand("jdev/sys/contextswitchesi", LongValue.class);

    /**
     * Current heap status.
     */
    public static final LoxoneMessageCommand<Heap> DEV_SYS_HEAP = jsonHttpCommand("jdev/sys/heap", Heap.class);

    /**
     * Current cpu load.
     */
    public static final LoxoneMessageCommand<PercentValue> DEV_SYS_CPU = jsonHttpCommand("jdev/sys/cpu", PercentValue.class);

    /**
     * Cumulative count of LAN transmitted packets.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXP = jsonHttpCommand("jdev/lan/txp", LongValue.class);

    /**
     * Cumulative count of LAN transmitted error packets.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXE = jsonHttpCommand("jdev/lan/txe", LongValue.class);

    /**
     * Cumulative count of LAN transmitted collision packets.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXC = jsonHttpCommand("jdev/lan/txc", LongValue.class);

    /**
     * Cumulative count of LAN buffer failures.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_EXH = jsonHttpCommand("jdev/lan/exh", LongValue.class);

    /**
     * Cumulative count of LAN underrun failures.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXU = jsonHttpCommand("jdev/lan/txu", LongValue.class);

    /**
     * Cumulative count of LAN received packets.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_RXP = jsonHttpCommand("jdev/lan/rxp", LongValue.class);

    /**
     * Cumulative count of LAN EOF failures.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_EOF = jsonHttpCommand("jdev/lan/eof", LongValue.class);

    /**
     * Cumulative count of LAN receive overrun failures.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_RXO = jsonHttpCommand("jdev/lan/rxo", LongValue.class);

    /**
     * Cumulative count of LAN no receive buffer failures.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_NOB = jsonHttpCommand("jdev/lan/nob", LongValue.class);

    /**
     * Cumulative count of Loxone BUS packets sent.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PACKETSSENT = jsonHttpCommand("jdev/bus/packetssent", LongValue.class);

    /**
     * Cumulative count of Loxone BUS packets received.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PACKETSRECEIVED = jsonHttpCommand("jdev/bus/packetsreceived", LongValue.class);

    /**
     * Cumulative count of Loxone BUS receive errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_RECEIVEERRORS = jsonHttpCommand("jdev/bus/receiveerrors", LongValue.class);

    /**
     * Cumulative count of Loxone BUS frame errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_FRAMEERRORS = jsonHttpCommand("jdev/bus/frameerrors", LongValue.class);

    /**
     * Cumulative count of Loxone BUS overrun errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_OVERRUNS = jsonHttpCommand("jdev/bus/overruns", LongValue.class);

    /**
     * Cumulative count of Loxone BUS parity errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PARITYERRORS = jsonHttpCommand("jdev/bus/parityerrors", LongValue.class);

    /**
     * Get the crypto key for given user.
     * @param user loxone user
     * @return command requesting user's crypto key
     */
    public static LoxoneMessageCommand<Hashing> getKey(final String user) {
        return jsonWsCommand("jdev/sys/getkey2/" + requireNonNull(user), Hashing.class);
    }

    /**
     * Get the visu hashing  for given user.
     * @param user loxone user
     * @return command requesting user's visu hashing
     */
    public static LoxoneMessageCommand<Hashing> getVisuHash(final String user) {
        return jsonWsCommand("jdev/sys/getvisusalt/" + requireNonNull(user), Hashing.class);
    }

    private final Class<V> valueType;

    @SuppressWarnings("unchecked")
    protected LoxoneMessageCommand(final String command, final Type type, final Class<V> valueType, final boolean httpSupported,
                                final boolean wsSupported) {
        super(command, type, (Class<LoxoneMessage<V>>) (Class<?>) LoxoneMessage.class, httpSupported, wsSupported);
        this.valueType = valueType;
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonHttpCommand(final String command, final Class<V> valueType) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, true, false);
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonWsCommand(final String command, final Class<V> valueType) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, false, true);
    }

    /**
     * List of known commands
     */
    public static List<LoxoneMessageCommand<?>> COMMANDS = Arrays.asList(
            DEV_BUS_FRAMEERRORS,
            DEV_BUS_OVERRUNS,
            DEV_BUS_PACKETSRECEIVED,
            DEV_BUS_PACKETSSENT,
            DEV_BUS_PARITYERRORS,
            DEV_BUS_RECEIVEERRORS,
            DEV_CFG_API,
            DEV_LAN_EOF,
            DEV_LAN_EXH,
            DEV_LAN_NOB,
            DEV_LAN_RXO,
            DEV_LAN_RXP,
            DEV_LAN_TXC,
            DEV_LAN_TXE,
            DEV_LAN_TXP,
            DEV_LAN_TXU,
            DEV_SYS_COMINTS,
            DEV_SYS_CONTEXTSWITCHESI,
            DEV_SYS_CONTEXTSWITCHES,
            DEV_SYS_CPU,
            DEV_SYS_GETPUBLICKEY,
            DEV_SYS_HEAP,
            DEV_SYS_INTS,
            DEV_SYS_LANINTS,
            DEV_SYS_NUMTASKS
    );

    /**
     * Type of the {@link LoxoneMessage} value
     * @return value type
     */
    public Class<V> getValueType() {
        return valueType;
    }

    /**
     * Ensures the given beeing not null and of this command {@link LoxoneMessageCommand#valueType}
     * @param value value to check
     * @return checked and cast value
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public V ensureValue(@Nullable final LoxoneValue value) {
        if (value != null && valueType.isAssignableFrom(value.getClass())) {
            return (V) value;
        } else {
            throw new LoxoneException("Expected value of type " + valueType);
        }
    }
}
