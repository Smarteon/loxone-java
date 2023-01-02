package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Specific {@link CommandResponseListener} for {@link LoxoneMessage}.
 */
public interface LoxoneMessageCommandResponseListener extends CommandResponseListener<LoxoneMessage<?>> {

    @Override
    default boolean accepts(final @NotNull Class<?> clazz) {
        return LoxoneMessage.class.isAssignableFrom(clazz);
    }

    /**
     * Whether this listener want's to accept error responses. Default implementation returns always false.
     * @return true if error responses should be processed, false otherwise.
     * @see LoxoneMessage#isSuccess()
     */
    default boolean acceptsErrorResponses() {
        return false;
    }
}
