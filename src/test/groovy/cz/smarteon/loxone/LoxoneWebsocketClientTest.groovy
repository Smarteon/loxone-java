package cz.smarteon.loxone

import spock.lang.Specification

import java.nio.ByteBuffer

class LoxoneWebsocketClientTest extends Specification {

    LoxoneWebsocketClient client

    void setup() {
        client = new LoxoneWebsocketClient(Mock(LoxoneWebSocket), URI.create(''))
    }

    def "should parse binary message"() {
        expect:
        client.onMessage(ByteBuffer.wrap([0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0] as byte[]))
    }
}
