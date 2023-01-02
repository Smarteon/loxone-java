package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Represents a list of {@link LoxoneUuid}.
 */
public class LoxoneUuids extends ArrayList<LoxoneUuid> {

    /**
     * Get and ensure the only uuid in this list.
     * @throws IllegalStateException in case this list doesn't contain single uuid.
     * @return the only uuid in this list, or throws exception
     */
    @NotNull
    public LoxoneUuid only() {
        if (hasOnlyOne()) {
            return get(0);
        } else {
            throw new IllegalStateException("There are no or too many uuids, not the only one");
        }
    }

    public boolean hasOnlyOne() {
        return size() == 1;
    }

}
