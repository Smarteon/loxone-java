package cz.smarteon.loxone

import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneValue
import cz.smarteon.loxone.message.PubKeyInfo
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Timeout

import static cz.smarteon.loxone.Protocol.C_JSON_API
import static cz.smarteon.loxone.Protocol.C_JSON_PUBLIC_KEY
import static org.hamcrest.CoreMatchers.equalTo

class LoxoneWebSocketTest extends Specification {

    MockWebSocketServer server
    LoxoneAuth authMock
    @Subject LoxoneWebSocket lws

    void setup() {
        server = new MockWebSocketServer()
        server.start()
        authMock = Stub(LoxoneAuth)
        def http = Stub(LoxoneHttp) {
            get(C_JSON_API) >> new LoxoneMessage(C_JSON_API, 200, new ApiInfo('50:4F:94:10:B8:4A', '9.1.10.30'))
            get(C_JSON_PUBLIC_KEY) >> new LoxoneMessage(C_JSON_PUBLIC_KEY, 200, new PubKeyInfo(MockWebSocketServer.PUBLIC_KEY))
        }
        lws = new LoxoneWebSocket("localhost:${server.port}", new LoxoneAuth(http, MockWebSocketServer.USER, MockWebSocketServer.PASS, 'visPass'))
    }

    void cleanup() {
        server.stop()
    }

    def "should send simple command"() {
        given:
        server.expect(equalTo('testCmd'))

        when:
        lws.sendCommand('testCmd')

        then:
        server.verifyExpectations(100)
    }

    @Timeout(2)
    def "should handle bad credentials"() {
        given:
        server.badCredentials = true

        expect:
        lws.sendCommand('baf')
    }
}
