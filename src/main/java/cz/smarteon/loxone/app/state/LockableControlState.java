package cz.smarteon.loxone.app.state;

import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.Loxone;
import cz.smarteon.loxone.app.Control;
import cz.smarteon.loxone.app.state.events.LockedEvent;
import cz.smarteon.loxone.message.TextEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Base class for the controlStates in loxone application that supports locking.
 * <p>
 * This class keeps track of the state of the control based on the events of the miniserver.
 * </p>
 * @param <T> The type of control this class keeps track of.
 */
@Slf4j
public abstract class LockableControlState<S, T extends Control> extends ControlState<S, T> {

    /**
     * Current state of the lock.
     */
    @Getter
    @Nullable
    private Locked locked;

    /**
     * Extra free format reason in case of lock.
     */
    @Getter
    @Nullable
    private String lockedReason;

    protected LockableControlState(Loxone loxone, T control) {
        super(loxone, control);
    }

    /**
     * Accepts TextEvents that can contain locking state updates for this control.
     * @param event text event received (should not be null)
     */
    @Override
    void accept(@NotNull TextEvent event) {
        if (event.getUuid().equals(getControl().stateLocked())) {
            processLockedEvent(event);
        }
    }

    /**
     * Process the TextEvent as a locked event message and update the state of the control accordingly.
     * @param event text event received
     */
    private void processLockedEvent(TextEvent event) {
        try {
            final boolean isCurrentState;
            if (event.getText().isEmpty()) {
                isCurrentState = Locked.NO.equals(locked) && lockedReason == null;
                this.locked = Locked.NO;
                this.lockedReason = null;
            } else {
                final LockedEvent lockedEvent = Codec.readMessage(event.getText(), LockedEvent.class);
                isCurrentState = lockedEvent.getLocked().equals(locked) && lockedEvent.getReason().equals(lockedReason);
                this.locked = lockedEvent.getLocked();
                this.lockedReason = lockedEvent.getReason();
            }
            if (!isCurrentState) {
                notifyStateChanged();
            }
        } catch (IOException e) {
            log.info("Unable to parse locked event!", e);
        }
    }
}
