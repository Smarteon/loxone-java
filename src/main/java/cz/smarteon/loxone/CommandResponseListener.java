package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;

/**
 * Allows to listen on messages representing {@link Command}s' responses.
 * @param <T> type of response message
 */
public interface CommandResponseListener<T> {
    enum State {
        /**
         * Message is read but can be processed by other listeners.
         */
        READ {
            @Override
            State fold(State next) {
                return next != IGNORED ? next : this;
            }
        },
        /**
         * Message is consumed in terms other listeners should not receive it.
         */
        CONSUMED {
            @Override
            State fold(State next) {
                return this;
            }
        },
        /**
         * Message is ignored - nor read or consumed.
         */
        IGNORED {
            @Override
            State fold(State next) {
                return next;
            }
        };

        abstract State fold(State next);
    }

    /**
     * Process response message of given command and returns {@link State}
     * @param command command
     * @param message message
     * @return state signaling the processing output
     */
    @NotNull
    State onCommand(@NotNull final Command<? extends T> command, @NotNull final T message);

    /**
     * Checks whether this listener accepts response of given type.
     *
     * @param clazz type to check
     * @return true if this listener accepts response of given type, false otherwise
     */
    boolean accepts(@NotNull final Class<?> clazz);
}
