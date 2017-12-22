package cz.smarteon.loxone.message;

import cz.smarteon.loxone.LoxoneUuid;

public class ValueEvent extends LoxoneEvent {

    public static final int PAYLOAD_LENGTH = 24;

    private final double value;

    public ValueEvent(final LoxoneUuid uuid, final double value) {
        super(uuid);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValueEvent{" +
                "uuid=" + uuid +
                ", value=" + value +
                '}';
    }
}
