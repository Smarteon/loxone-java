package cz.smarteon.loxone.discovery;

import org.java_websocket.util.NamedThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Provides Loxone miniserver discovery, using UDP.
 * Can throw {@link LoxoneDiscoveryException} if something doesn't work correctly.
 *
 * It uses UDP discovery exchange, reverse engineered and documented
 * <a href="https://github.com/sarnau/Inside-The-Loxone-Miniserver/blob/master/LoxoneMiniserverNetworking.md">here</a>.
 *
 * <ol>
 *     <li>UDP broadcast on LAN (255.255.255.255) port 7070 with single zero byte payload</li>
 *     <li>The response is expected on UDP port 7071</li>
 * </ol>
 */
public class MiniserverDiscoverer {

    private static final byte[] DISCOVERY_PACKET_PAYLOAD = new byte[] { 0x00 };
    private static final int REQUEST_PORT = 7070;
    private static final int RESPONSE_PORT = 7071;

    private final ExecutorService executor;
    private final InetAddress broadcastAddress;
    private int requestPort = REQUEST_PORT;
    private int responsePort = RESPONSE_PORT;


    /**
     * Creates new instance, uses internal executor for UDP listener.
     */
    public MiniserverDiscoverer() {
        this(null);
    }

    /**
     * Creates new instance, uses given executor for UDP listener.
     * @param executor executor for UDP listener
     */
    public MiniserverDiscoverer(final @Nullable ExecutorService executor) {
        this.executor = executor;
        try {
            this.broadcastAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new LoxoneDiscoveryException("Can't get broadcast address", e);
        }
    }

    @TestOnly
    MiniserverDiscoverer(final int requestPort, final int responsePort) {
        this();
        this.requestPort = requestPort;
        this.responsePort = responsePort;
    }

    /**
     * Do the discovery. Ends after given limit if miniserver is discovered or after given timeout milliseconds.
     * @param limit finish after discovering limiting number of miniservers
     * @param timeoutMillis finish after timeout
     * @return set of discovered miniservers, can be empty in case no response is received in given timeout
     */
    @NotNull
    public Set<MiniserverDiscovery> discover(int limit, int timeoutMillis) {
        final Set<MiniserverDiscovery> discoveries = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch(limit);
        final ExecutorService currentExecutor = executor != null
                ? executor
                : Executors.newSingleThreadExecutor(new NamedThreadFactory(MiniserverDiscoverer.class.getSimpleName()));

        Future<?> listenerFuture = null;
        DiscoveryListener discoveryListener = null;

        try {
            discoveryListener = new DiscoveryListener( discovery -> {
                discoveries.add(discovery);
                latch.countDown();
            });

            listenerFuture = currentExecutor.submit(discoveryListener);
            sendDiscoveryPacket();

            latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // swallowed
        } catch (SocketException socketException) {
            socketException.printStackTrace();
        } finally {
            if (discoveryListener != null) {
                discoveryListener.stop();
            }
        }

        if (listenerFuture != null) {
            try {
                listenerFuture.get();
            } catch (ExecutionException e) {
                throw new LoxoneDiscoveryException("Problem listening for discovery response", e);
            } catch (InterruptedException e) {
                // swallowed
            } finally {
                if (executor == null) {
                    currentExecutor.shutdownNow();
                }
            }
        }

        return discoveries;
    }

    private void sendDiscoveryPacket() throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        try {
            DatagramPacket packet = new DatagramPacket(
                    DISCOVERY_PACKET_PAYLOAD,
                    DISCOVERY_PACKET_PAYLOAD.length,
                    broadcastAddress,
                    requestPort
            );
            socket.send(packet);
        } catch (IOException ioException) {
            throw new LoxoneDiscoveryException("Can't send discovery request", ioException);
        } finally {
            socket.close();
        }
    }

    private class DiscoveryListener implements Runnable {

        private static final int SOCKET_TIMEOUT = 20;

        private final DatagramSocket socket;
        private final Consumer<MiniserverDiscovery> callback;
        private final AtomicBoolean running = new AtomicBoolean(false);

        DiscoveryListener(final Consumer<MiniserverDiscovery> callback) throws SocketException {
            this.socket = new DatagramSocket(responsePort);
            this.socket.setSoTimeout(SOCKET_TIMEOUT);
            this.callback = callback;
        }

        @Override
        public void run() {
            running.compareAndSet(false, true);
            try {
                while (running.get()) {
                    final byte[] buffer = new byte[256];
                    final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(packet);
                        callback.accept(MiniserverDiscovery.fromResponse(new String(buffer)));
                    } catch (SocketTimeoutException expected) {
                        // just continue
                    } catch (IOException ioException) {
                        throw new LoxoneDiscoveryException("Cannot receive discovery response", ioException);
                    }
                }
            } finally {
                socket.close();
            }
        }

        public void stop() {
           running.compareAndSet(true, false);
        }
    }
}
