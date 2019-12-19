package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

/**
 * Loxone event carrying numeric value.
 */
public class ValueEvent extends LoxoneEvent {

    public static final int PAYLOAD_LENGTH = 24;

    private final double value;

    /**
     * Creates new instance
     * @param uuid event uuid
     * @param value carried value
     */
    public ValueEvent(final @NotNull LoxoneUuid uuid, final double value) {
        super(uuid);
        this.value = value;
    }

    /**
     * Carried numeric value
     * @return numeric value
     */
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValueEvent{" +
                "uuid=" + uuid +
                ", value=" + value +
                '}';
    }
}
