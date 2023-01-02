package cz.smarteon.loxone;

import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Allows to react on on loxone events.
 * @see LoxoneWebSocket#registerListener(LoxoneEventListener)
 */
public interface LoxoneEventListener {

    /**
     * Receives {@link ValueEvent}.
     * @param event value event received (should not be null)
     */
    default void onEvent(final @NotNull ValueEvent event) { }

    /**
     * Receives {@link TextEvent}.
     * @param event text event received (should not be null)
     */
    default void onEvent(final @NotNull TextEvent event) { }
}
