package cz.smarteon.loxone.message;

import cz.smarteon.loxone.Command;
import cz.smarteon.loxone.LoxoneException;
import cz.smarteon.loxone.app.MiniserverType;
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
    public static final LoxoneMessageCommand<ApiInfo> DEV_CFG_API =
            jsonHttpCommand("jdev/cfg/api", ApiInfo.class, MiniserverType.ALL);

    /**
     * Miniserver Application version. Returns date of last application modification.
     */
    public static final LoxoneMessageCommand<DateValue> LOX_APP_VERSION =
            jsonCommand("jdev/sps/LoxAPPversion3", DateValue.class, MiniserverType.ALL);

    /**
     * API's public key.
     */
    public static final LoxoneMessageCommand<PubKeyInfo> DEV_SYS_GETPUBLICKEY =
            jsonHttpCommand("jdev/sys/getPublicKey", PubKeyInfo.class, MiniserverType.KNOWN);

    /**
     * Number of system tasks.
     */
    public static final LoxoneMessageCommand<IntValue> DEV_SYS_NUMTASKS =
            jsonHttpCommand("jdev/sys/numtasks", IntValue.class, MiniserverType.KNOWN);

    /**
     * Number of system interruptions. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_INTS =
            jsonHttpCommand("jdev/sys/ints", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Number of system communication interruptions. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_COMINTS =
            jsonHttpCommand("jdev/sys/comints", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Number of system LAN interruptions. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_LANINTS =
            jsonHttpCommand("jdev/sys/lanints", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Number of system context switches. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_CONTEXTSWITCHES =
            jsonHttpCommand("jdev/sys/contextswitches", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Number of system context switches of interrupted tasks. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_SYS_CONTEXTSWITCHESI =
            jsonHttpCommand("jdev/sys/contextswitchesi", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Current heap status.
     */
    public static final LoxoneMessageCommand<Heap> DEV_SYS_HEAP =
            jsonHttpCommand("jdev/sys/heap", Heap.class, MiniserverType.KNOWN);

    /**
     * Current cpu load.
     */
    public static final LoxoneMessageCommand<PercentValue> DEV_SYS_CPU =
            jsonHttpCommand("jdev/sys/cpu", PercentValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of LAN transmitted packets. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXP =
            jsonHttpCommand("jdev/lan/txp", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN transmitted error packets. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXE =
            jsonHttpCommand("jdev/lan/txe", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN transmitted collision packets. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXC =
            jsonHttpCommand("jdev/lan/txc", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN buffer failures. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_EXH =
            jsonHttpCommand("jdev/lan/exh", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN underrun failures. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_TXU =
            jsonHttpCommand("jdev/lan/txu", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN received packets. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_RXP =
            jsonHttpCommand("jdev/lan/rxp", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN EOF failures. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_EOF =
            jsonHttpCommand("jdev/lan/eof", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN receive overrun failures. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_RXO =
            jsonHttpCommand("jdev/lan/rxo", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of LAN no receive buffer failures. Not supported by miniserver Gen2.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_LAN_NOB =
            jsonHttpCommand("jdev/lan/nob", LongValue.class, MiniserverType.FIRST_GEN);

    /**
     * Cumulative count of Loxone BUS packets sent.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PACKETSSENT =
            jsonHttpCommand("jdev/bus/packetssent", LongValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of Loxone BUS packets received.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PACKETSRECEIVED =
            jsonHttpCommand("jdev/bus/packetsreceived", LongValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of Loxone BUS receive errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_RECEIVEERRORS =
            jsonHttpCommand("jdev/bus/receiveerrors", LongValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of Loxone BUS frame errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_FRAMEERRORS =
            jsonHttpCommand("jdev/bus/frameerrors", LongValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of Loxone BUS overrun errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_OVERRUNS =
            jsonHttpCommand("jdev/bus/overruns", LongValue.class, MiniserverType.KNOWN);

    /**
     * Cumulative count of Loxone BUS parity errors.
     */
    public static final LoxoneMessageCommand<LongValue> DEV_BUS_PARITYERRORS =
            jsonHttpCommand("jdev/bus/parityerrors", LongValue.class, MiniserverType.KNOWN);

    /**
     * List of known commands.
     */
    public static final List<LoxoneMessageCommand<?>> COMMANDS = Arrays.asList(
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
            DEV_SYS_NUMTASKS,
            LOX_APP_VERSION
    );

    private final Class<V> valueType;

    @SuppressWarnings("unchecked")
    protected LoxoneMessageCommand(final String command,
                                   final Type type,
                                   final Class<V> valueType,
                                   final boolean httpSupported,
                                   final boolean wsSupported,
                                   final MiniserverType[] supportedMiniservers) {
        super(command, type, (Class<LoxoneMessage<V>>) (Class<?>) LoxoneMessage.class,
                httpSupported, wsSupported, supportedMiniservers);
        this.valueType = valueType;
    }

    /**
     * Get the crypto key for given user.
     * @param user loxone user
     * @return command requesting user's crypto key
     */
    public static LoxoneMessageCommand<Hashing> getKey(final String user) {
        return jsonWsCommand("jdev/sys/getkey2/" + requireNonNull(user), Hashing.class, MiniserverType.KNOWN);
    }

    /**
     * Get the visu hashing  for given user.
     * @param user loxone user
     * @return command requesting user's visu hashing
     */
    public static LoxoneMessageCommand<Hashing> getVisuHash(final String user) {
        return jsonWsCommand("jdev/sys/getvisusalt/" + requireNonNull(user), Hashing.class, MiniserverType.KNOWN);
    }

    /**
     * Get 1-wire details by given extension serial number.
     * @param extensionSerial serial number of 1-wire extension
     * @return command requesting 1-wire details
     */
    public static LoxoneMessageCommand<OneWireDetails> oneWireDetails(final @NotNull String extensionSerial) {
        return jsonCommand("jdev/sys/ExtStatistics/" + requireNonNull(extensionSerial, "extensionSerial can't be null"),
                OneWireDetails.class, MiniserverType.KNOWN);
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonCommand(
            final String command, final Class<V> valueType, final MiniserverType[] supportedMiniservers) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, true, true, supportedMiniservers);
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonHttpCommand(
            final String command, final Class<V> valueType, final MiniserverType[] supportedMiniservers) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, true, false, supportedMiniservers);
    }

    private static <V extends LoxoneValue> LoxoneMessageCommand<V> jsonWsCommand(
            final String command, final Class<V> valueType, final MiniserverType[] supportedMiniservers) {
        return new LoxoneMessageCommand<>(command, Type.JSON, valueType, false, true, supportedMiniservers);
    }

    /**
     * Type of the {@link LoxoneMessage} value.
     * @return value type
     */
    public Class<V> getValueType() {
        return valueType;
    }

    /**
     * Ensures the given beeing not null and of this command {@link LoxoneMessageCommand#valueType}.
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
