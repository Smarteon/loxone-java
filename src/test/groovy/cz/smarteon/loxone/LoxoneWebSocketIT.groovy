package cz.smarteon.loxone

import cz.smarteon.loxone.MockWebSocketServer.MockWebSocketServerListener
import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.PubKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Timeout
import spock.util.concurrent.PollingConditions

import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static cz.smarteon.loxone.Command.voidWsCommand
import static cz.smarteon.loxone.CryptoSupport.PASS
import static cz.smarteon.loxone.CryptoSupport.PUBLIC_KEY
import static cz.smarteon.loxone.CryptoSupport.USER
import static cz.smarteon.loxone.CryptoSupport.VISU_PASS
import static cz.smarteon.loxone.app.MiniserverType.KNOWN
import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY
import static org.hamcrest.CoreMatchers.equalTo

class LoxoneWebSocketIT extends Specification {

    MockWebSocketServer server
    LoxoneAuth authMock
    @Subject LoxoneWebSocket lws
    MockWebSocketServerListener listener = new MockWebSocketServerListener() {

        @Override
        void started() {
            listenerLatch.countDown()
        }

        @Override
        void stopped() {
            listenerLatch.countDown()
        }
    }
    CountDownLatch listenerLatch

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())
    }

    void setup() {
        server = new MockWebSocketServer(listener, 20)
        startServer()
        authMock = Stub(LoxoneAuth)
        def http = Stub(LoxoneHttp) {
            get(DEV_CFG_API) >> new LoxoneMessage(DEV_CFG_API.command, 200, new ApiInfo('50:4F:94:10:B8:4A', '9.1.10.30'))
            get(DEV_SYS_GETPUBLICKEY) >> new LoxoneMessage(DEV_SYS_GETPUBLICKEY.command, 200, new PubKeyInfo(PUBLIC_KEY))
        }
        lws = new LoxoneWebSocket(new LoxoneEndpoint('localhost', server.port), new LoxoneAuth(http, USER, PASS, VISU_PASS))
        lws.setAuthTimeoutSeconds(1)
    }

    void cleanup() {
        stopServer()
        lws.close()
    }

    void startServer() {
        listenerLatch = new CountDownLatch(1)
        server.start()
        listenerLatch.await(500, TimeUnit.MILLISECONDS)
    }

    void stopServer() {
        listenerLatch = new CountDownLatch(1)
        server.stop()
        listenerLatch.await(500, TimeUnit.MILLISECONDS)
    }

    def "should send simple command"() {
        given:
        def condition = new PollingConditions(initialDelay: 0.1, delay: 0.02)
        server.expect(equalTo('testCmd'))

        when:
        lws.sendCommand(voidWsCommand(KNOWN, 'testCmd'))

        then:
        condition.eventually {
            server.verifyExpectations()
        }
    }

    def "should send secure command"() {
        given:
        def condition = new PollingConditions(initialDelay: 0.1, delay: 0.02)
        server.expect(equalTo('testUuid/pulse'))

        when:
        lws.sendSecureCommand(genericControlCommand('testUuid', 'pulse'))

        then:
        condition.eventually {
            server.verifyExpectations()
        }
    }

    @Timeout(2)
    def "should handle bad credentials"() {
        given:
        lws.retries = 0
        server.badCredentials = 1

        when:
        lws.sendCommand(voidWsCommand(KNOWN, 'baf'))

        then:
        thrown(LoxoneException)
    }

//    @Ignore("Unreliable test since it's impossible to detect when the same port is again free to bind")
    def "should handle server restart"() {
        when:
        def beforeRestartCondition = new PollingConditions(initialDelay: 0.1, delay: 0.02)
        server.expect(equalTo('beforeRestart'))
        lws.sendCommand(voidWsCommand(KNOWN, 'beforeRestart'))

        then:
        beforeRestartCondition.eventually {
            server.verifyExpectations()
        }

        when:
        def afterRestartCondition = new PollingConditions(initialDelay: 0.1, delay: 0.02)
        stopServer()
        server = new MockWebSocketServer(server)
        server.expect(equalTo('afterRestart'))
        startServer()
        lws.sendCommand(voidWsCommand(KNOWN, 'afterRestart'))

        then:
        afterRestartCondition.eventually {
            server.verifyExpectations()
        }
    }

    def "should retry on bad credentials"() {
        given:
        def condition = new PollingConditions(initialDelay: 0.1, delay: 0.05, timeout: 10)
        server.expect(equalTo('baf'))
        lws.retries = 5
        server.badCredentials = 4

        when:
        lws.sendCommand(voidWsCommand(KNOWN, 'baf'))

        then:
        condition.eventually {
            server.verifyExpectations()
        }
    }
}
