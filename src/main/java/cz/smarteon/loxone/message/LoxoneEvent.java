package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Common predecessor of loxone events.
 */
public abstract class LoxoneEvent {

    protected final LoxoneUuid uuid;

    /**
     * New instance
     * @param uuid event uuid
     */
    protected LoxoneEvent(final @NotNull LoxoneUuid uuid) {
        this.uuid = requireNonNull(uuid, "uuid can't be null");
    }

    /**
     * Get event uuid
     * @return event uuid
     */
    @NotNull
    public LoxoneUuid getUuid() {
        return uuid;
    }
}
