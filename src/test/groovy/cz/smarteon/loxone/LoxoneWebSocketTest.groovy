package cz.smarteon.loxone

import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.PubKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.java_websocket.client.WebSocketClient
import spock.lang.Specification
import spock.lang.Subject

import java.security.Security

import static cz.smarteon.loxone.CryptoSupport.PUBLIC_KEY
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY

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
        def http = Stub(LoxoneHttp) {
            get(DEV_CFG_API) >> new LoxoneMessage(DEV_CFG_API.command, 200, new ApiInfo('50:4F:94:10:B8:4A', '9.1.10.30'))
            get(DEV_SYS_GETPUBLICKEY) >> new LoxoneMessage(DEV_SYS_GETPUBLICKEY.command, 200, new PubKeyInfo(PUBLIC_KEY))
        }
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

    def "should call websocket listener"() {
        given:
        def listener = Mock(LoxoneWebSocketListener)

        when:
        loxoneWebSocket.setWebSocketListener(listener)
        loxoneWebSocket.connectionOpened()

        then:
        loxoneWebSocket.getWebSocketListener() == listener
        1 * listener.webSocketOpened()

    }
}
