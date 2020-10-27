package cz.smarteon.loxone

import spock.lang.Specification
import spock.lang.Subject

import java.nio.ByteBuffer

class LoxoneWebsocketClientTest extends Specification {

    @Subject LoxoneWebsocketClient client
    LoxoneWebSocket webSocket

    void setup() {
        webSocket = Mock(LoxoneWebSocket)
        client = new LoxoneWebsocketClient(webSocket, URI.create(''))
    }

    def "should parse binary message"() {
        expect:
        client.onMessage(ByteBuffer.wrap([0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0] as byte[]))
    }

    def "should handle onClose"() {
        when:
        client.onClose(1000, 'some reason', remote)

        then:
        1 * webSocket.wsClosed()
        1 * webSocket.connectionClosed(1000, remote)
        if (remote) {
            1 * webSocket.autoRestart()
        }

        where:
        remote << [true, false]
    }
}
