package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.Nullable;

/**
 * Represents updatable loxone component.
 */
public interface Updatable {

    /**
     * Whether the component is currently updating itself.
     * @return true when update is in progress, false otherwise
     */
    boolean isUpdating();

    /**
     * If the component {@link #isUpdating()} get the progress of update (usually the percentage).
     * @return the percentage of update progress or null when it's not updating
     */
    @Nullable
    Integer getUpdateProgress();
}
