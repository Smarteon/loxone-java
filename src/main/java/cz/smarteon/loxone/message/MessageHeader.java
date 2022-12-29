package cz.smarteon.loxone.message;

import java.util.Objects;

/**
 * Represents the first part of loxone message.
 */
public final class MessageHeader {

    public static final int PAYLOAD_LENGTH = 8;
    public static final byte FIRST_BYTE = 0x03;

    /**
     * Represent special case of KEEP_ALIVE header, always of {@link MessageKind#KEEP_ALIVE} kind
     * with non estimated size 0.
     */
    public static final MessageHeader KEEP_ALIVE = new MessageHeader(MessageKind.KEEP_ALIVE, false, 0);

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
        return "MessageHeader{"
                + "kind=" + kind
                + ", sizeEstimated=" + sizeEstimated
                + ", messageSize=" + messageSize
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageHeader that = (MessageHeader) o;
        return sizeEstimated == that.sizeEstimated
                && messageSize == that.messageSize
                && kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, sizeEstimated, messageSize);
    }
}
