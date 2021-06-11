package cz.smarteon.loxone.discovery

import spock.lang.Specification
import spock.lang.Timeout

class MiniserverDiscovererTest extends Specification {

    DiscoverableMockMiniserver miniserver = new DiscoverableMockMiniserver()

    void setup() {
        miniserver.start()
    }

    void cleanup() {
        miniserver.shutdown()
    }

    @Timeout(2000)
    def "should discover"() {
        when:
        def discoveries = new MiniserverDiscoverer(7075, 7076).discover(1, 1000)

        then:
        discoveries.size() == 1
        discoveries.first().name == 'baf'
    }

    @Timeout(2000)
    def "finish when there is nothing to discover"() {
        given:
        miniserver.shouldAnswer = false

        expect:
        new MiniserverDiscoverer(7075, 7076).discover(1, 500).isEmpty()
    }

    private class DiscoverableMockMiniserver extends Thread {

        boolean shouldAnswer = true

        private volatile boolean running = true

        private static answer = ('LoxLIVE: baf 192.168.5.1:80 504F94112134 12.0.2.24 Prog:2021-06-01 23:40:59 Type:0 ' +
                'HwId:A0000 IPv6:,00000000:0/X,13106567:11021110/O,0cd87947:11010825/O,05d8726f:12010322/O').getBytes()

        void shutdown() {
            running = false
            try {
                join()
            } catch(InterruptedException ignore) {}
        }

        @Override
        void run() {
            def socket = new DatagramSocket(7075)
            socket.setSoTimeout(100)

            try {
                while (running) {
                    try {
                        def buffer = new byte[1];
                        def packet = new DatagramPacket(buffer, buffer.length)
                        socket.receive(packet)
                        if (shouldAnswer && ((byte) 0x00) == buffer[0]) {
                            def answerPacket = new DatagramPacket(answer, answer.length, packet.getAddress(), 7076)
                            socket.send(answerPacket)
                        }
                    } catch (SocketTimeoutException ignore) {}
                }
            } finally {
                socket.close()
            }
        }
    }
}
