package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Loxone event carrying text value.
 */
@Getter
@ToString(callSuper = true)
public class TextEvent extends LoxoneEvent {

    /**
     * Icon uuid.
     */
    private final LoxoneUuid iconUuid;

    /**
     * Carried text value.
     */
    private final String text;

    /**
     * Creates new instance.
     * @param uuid event uuid
     * @param iconUuid icon uuid
     * @param text text value
     */
    public TextEvent(final @NotNull LoxoneUuid uuid, final @NotNull LoxoneUuid iconUuid, final @NotNull String text) {
        super(uuid);
        this.iconUuid = requireNonNull(iconUuid, "iconUuid can't be null");
        this.text = requireNonNull(text, "text can't be null");
    }
}
