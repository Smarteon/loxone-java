package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Loxone event carrying text value.
 */
public class TextEvent extends LoxoneEvent {
    private final LoxoneUuid iconUuid;
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

    /**
     * Icon uuid
     * @return icon uuid
     */
    @NotNull
    public LoxoneUuid getIconUuid() {
        return iconUuid;
    }

    /**
     * Carried text value
     * @return text value
     */
    @NotNull
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TextEvent{" +
                "uuid=" + uuid +
                ", iconUuid=" + iconUuid +
                ", text='" + text + '\'' +
                '}';
    }
}
