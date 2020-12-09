package cz.smarteon.loxone


import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.java_websocket.client.WebSocketClient
import spock.lang.Specification
import spock.lang.Subject

import java.security.Security
import java.util.function.Function

class LoxoneWebSocketTest extends Specification {

    LoxoneAuth authMock
    WebSocketClient wsClientMock
    AuthListener authListener
    CommandSender commandSender
    def scheduler
    @Subject LoxoneWebSocket loxoneWebSocket

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())
    }

    void setup() {
        authMock = Mock(LoxoneAuth) {
            registerAuthListener(*_) >> {args -> authListener = args[0]}
            setCommandSender(*_) >> {args -> commandSender = args[0]}
            setAutoRefreshScheduler(*_) >> {args -> scheduler = args[0]}
        }
        wsClientMock = Mock(WebSocketClient)
        loxoneWebSocket = new LoxoneWebSocket(new LoxoneEndpoint('localhost', 12345), authMock,
                { lws, uri -> wsClientMock})
        loxoneWebSocket.setAuthTimeoutSeconds(1)
    }

    void cleanup() {
        loxoneWebSocket.close()
    }

    def "should set loxone auth interop"() {
        expect:
        authListener != null
        commandSender != null
        scheduler != null
    }

    def "should auto restart"() {
        given:
        loxoneWebSocket.setAutoRestart(true)
        loxoneWebSocket.setRetries(0)
        loxoneWebSocket.setAuthTimeoutSeconds(1)

        when:
        loxoneWebSocket.sendCommand(Command.LOX_APP)

        then:
        loxoneWebSocket.retries == 0
        loxoneWebSocket.authTimeoutSeconds == 1
        1 * authMock.isInitialized() >> true
        1 * wsClientMock.connect() >> { loxoneWebSocket.connectionOpened() }
        1 * authMock.startAuthentication() >> { authListener.authCompleted() }

        when:
        loxoneWebSocket.autoRestart()
        sleep(2100)

        then:
        1 * authMock.isInitialized() >> true
    }

    def "should call websocket listener opened"() {
        given:
        def listener = Mock(LoxoneWebSocketListener)

        when:
        loxoneWebSocket.registerWebSocketListener(listener)
        loxoneWebSocket.connectionOpened()
        sleep(10) // wait for another thread execution

        then:
        1 * listener.webSocketOpened()
    }

    def "should call websocket listener closed"() {
        given:
        def listener = Mock(LoxoneWebSocketListener)

        when:
        loxoneWebSocket.registerWebSocketListener(listener)
        loxoneWebSocket.connectionClosed(1000, remote)
        sleep(10) // wait for another thread execution

        then:
        if (remote)
            1 * listener.webSocketRemoteClosed(1000)
        else
            1 * listener.webSocketLocalClosed(1000)

        where:
        remote << [true, false]
    }

    def "should close properly"() {
        given:
        loxoneWebSocket.setRetries(0)

        when:
        loxoneWebSocket.sendCommand(Command.LOX_APP)
        loxoneWebSocket.close()

        then:
        1 * wsClientMock.connect() >> { loxoneWebSocket.connectionOpened() }
        1 * authMock.startAuthentication() >> { authListener.authCompleted() }
        1 * wsClientMock.closeBlocking()

        loxoneWebSocket.scheduler.shutdown
    }

    def "should close properly when ws interrupted"() {
        given:
        loxoneWebSocket.setRetries(0)

        when:
        loxoneWebSocket.sendCommand(Command.LOX_APP)
        loxoneWebSocket.close()

        then:
        1 * wsClientMock.connect() >> { loxoneWebSocket.connectionOpened() }
        1 * authMock.startAuthentication() >> { authListener.authCompleted() }
        1 * wsClientMock.closeBlocking() >> { throw new InterruptedException('Testing interrupt') }

        loxoneWebSocket.scheduler.shutdown

        thrown(LoxoneException)
    }
}
