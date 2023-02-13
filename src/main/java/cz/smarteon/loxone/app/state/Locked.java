package cz.smarteon.loxone.app.state;

/**
 * Enum containing the different lock types for lockable controls.
 */
public enum Locked {

    /**
     * No lock present on the control.
     */
    NO,

    /**
     * Locked by ui (api).
     */
    UI,

    /**
     * Locked by logic in miniserver.
     */
    LOGIC
}
