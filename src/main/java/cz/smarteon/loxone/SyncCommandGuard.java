package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SyncCommandGuard<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncCommandGuard.class);

    private final CountDownLatch latch;

    private final Command<T> command;

    private Object response;

    SyncCommandGuard(final Command<T> command) {
        this.command = command;
        latch = new CountDownLatch(1);
    }

    @SuppressWarnings("unchecked")
    T waitForResponse(int seconds) {
        try {
            if (latch.await(seconds, TimeUnit.SECONDS)) {
                try {
                    return (T) response;
                } catch (ClassCastException cce) {
                    if (response instanceof LoxoneMessage<?>) {
                        LoxoneMessage<?> error = (LoxoneMessage<?>) response;
                        throw new LoxoneException("Error received of " + error.getControl() + " code " + error.getCode());
                    } else {
                        throw new LoxoneException("Unrecognizable error received to " + command.getCommand());
                    }
                }
            } else {
                throw new LoxoneException("Timeout waiting for sync command response " + command.getCommand());
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting for sync command request completion", e);
            throw new LoxoneException("Interrupted while waiting for sync command request completion");
        }
    }

    void receive(final Object response) {
        this.response = response;
        latch.countDown();
    }

    Command<T> getCommand() {
        return command;
    }
}
