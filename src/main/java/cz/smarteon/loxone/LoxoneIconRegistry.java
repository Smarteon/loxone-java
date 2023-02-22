package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ImageCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class LoxoneIconRegistry implements CommandRequestResponseListener<ByteBuffer> {

    private static final Logger LOG = LoggerFactory.getLogger(LoxoneIconRegistry.class);

    private final Map<String, ByteBuffer> iconMap = new HashMap<>();
    private LoxoneWebSocket loxoneWebSocket;

    private CountDownLatch iconLatch;

    @Override
    public void registerWebSocket(final @NotNull LoxoneWebSocket loxoneWebSocket) {
        this.loxoneWebSocket = loxoneWebSocket;
    }

    @Nullable
    public synchronized ByteBuffer getIcon(final @NotNull String iconUuid) {
        requireNonNull(loxoneWebSocket, "LoxoneWebSocket should be available!");
        if (iconMap.containsKey(iconUuid)) {
            return iconMap.get(iconUuid);
        } else {
            iconLatch = new CountDownLatch(1);
            loxoneWebSocket.sendCommand(ImageCommand.genericControlCommand(iconUuid));
            try {
                final int timeout = loxoneWebSocket.getAuthTimeoutSeconds() * loxoneWebSocket.getRetries() + 1;
                if (iconLatch.await(timeout, TimeUnit.SECONDS)) {
                    return iconMap.get(iconUuid);
                } else {
                    LOG.error("Loxone icon wasn't fetched within timeout");
                    throw new LoxoneException("Loxone icon wasn't fetched within timeout");
                }
            } catch (InterruptedException e) {
                LOG.error("Interrupted while waiting for loxone icon fetch", e);
                throw new LoxoneException("Interrupted while waiting for loxone icon fetch", e);
            }
        }
    }

    @Override
    public @NotNull State onCommand(@NotNull Command<? extends ByteBuffer> command, @NotNull ByteBuffer message) {
        ByteBuffer byteBuffer = command.ensureResponse(message);
        iconMap.put(command.getCommand(), byteBuffer);
        if (iconLatch != null) {
            iconLatch.countDown();
        }
        return State.READ;
    }

    @Override
    public boolean accepts(@NotNull Class<?> clazz) {
        return ByteBuffer.class.isAssignableFrom(clazz);
    }
}
