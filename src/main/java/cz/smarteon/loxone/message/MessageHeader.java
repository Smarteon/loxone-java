package cz.smarteon.loxone.message;

public class MessageHeader {

    public static final int PAYLOAD_LENGTH = 8;
    public static final byte FIRST_BYTE = 0x03;

    private final MessageKind kind;
    private final boolean sizeEstimated;
    private final long messageSize;


    public MessageHeader(MessageKind kind, boolean sizeEstimated, long messageSize) {
        this.kind = kind;
        this.sizeEstimated = sizeEstimated;
        this.messageSize = messageSize;
    }

    public MessageKind getKind() {
        return kind;
    }

    public boolean isSizeEstimated() {
        return sizeEstimated;
    }

    public long getMessageSize() {
        return messageSize;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "kind=" + kind +
                ", sizeEstimated=" + sizeEstimated +
                ", messageSize=" + messageSize +
                '}';
    }
}
