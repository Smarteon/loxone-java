package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Common predecessor of loxone events.
 */
@Getter
@ToString
public abstract class LoxoneEvent {

    /**
     * Event uuid
     */
    @NotNull
    protected final LoxoneUuid uuid;

    /**
     * New instance
     * @param uuid event uuid
     */
    protected LoxoneEvent(final @NotNull LoxoneUuid uuid) {
        this.uuid = requireNonNull(uuid, "uuid can't be null");
    }
}
