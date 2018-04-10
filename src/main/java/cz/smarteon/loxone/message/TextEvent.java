package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;

public class TextEvent extends LoxoneEvent {
    private final LoxoneUuid iconUuid;
    private final String text;

    public TextEvent(final LoxoneUuid uuid, final LoxoneUuid iconUuid, final String text) {
        super(uuid);
        this.iconUuid = iconUuid;
        this.text = text;
    }

    public LoxoneUuid getIconUuid() {
        return iconUuid;
    }

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
