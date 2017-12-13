package cz.smarteon.loxone.message;

public abstract class LoxoneEvent {

    protected final LoxoneUuid uuid;

    protected LoxoneEvent(final LoxoneUuid uuid) {
        this.uuid = uuid;
    }

    public LoxoneUuid getUuid() {
        return uuid;
    }
}
