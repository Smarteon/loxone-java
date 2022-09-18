package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Loxone event carrying numeric value.
 */
@Getter
@ToString(callSuper = true)
public class ValueEvent extends LoxoneEvent {

    public static final int PAYLOAD_LENGTH = 24;

    /**
     * Carried numeric value.
     */
    private final double value;

    /**
     * Creates new instance.
     * @param uuid event uuid
     * @param value carried value
     */
    public ValueEvent(final @NotNull LoxoneUuid uuid, final double value) {
        super(uuid);
        this.value = value;
    }
}
