package cz.smarteon.loxone.app.state.events;

import cz.smarteon.loxone.app.state.Locked;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of Loxone locked event.
 */
@Value
@Builder
@Jacksonized
public class LockedEvent {

    @Builder.Default
    Locked locked = Locked.NO;

    String reason;
}
