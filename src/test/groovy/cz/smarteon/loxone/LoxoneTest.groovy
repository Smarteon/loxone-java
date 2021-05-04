package cz.smarteon.loxone

import cz.smarteon.loxone.app.Control
import cz.smarteon.loxone.app.LoxoneApp
import cz.smarteon.loxone.message.ControlCommand
import spock.lang.Specification
import spock.lang.Subject

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand

class LoxoneTest extends Specification {

    @Subject Loxone loxone

    LoxoneAuth auth
    LoxoneWebSocket webSocket
    LoxoneHttp http
    CommandResponseListener appCmdListener

    void setup() {
        http = Mock(LoxoneHttp)
        webSocket = Mock(LoxoneWebSocket) {
            registerListener(*_) >> { args -> appCmdListener = args[0] }
        }
        auth = Mock(LoxoneAuth)

        loxone = new Loxone(http, webSocket, auth)
    }

    def "should initialize"() {
        expect:
        loxone.auth() == auth
        loxone.webSocket() == webSocket
        loxone.http() == http
        loxone.allMiniserversHttp().size() == 1
        loxone.clientMiniserversHttp().isEmpty()
        appCmdListener != null
    }

    def "test basic flow"() {
        given:
        def app = Mock(LoxoneApp)
        def appListener = Mock(LoxoneAppListener)
        def control  = Stub(Control) {
            getUuid() >> new LoxoneUuid('1177b172-020b-0b06-ffffc0f606ef595c')
            isSecured() >> false
        }
        def secControl  = Stub(Control) {
            getUuid() >> new LoxoneUuid('1177b172-020b-0b06-ffffc0f606ef595c')
            isSecured() >> true
        }

        when:
        loxone.setEventsEnabled(true)
        loxone.start()

        then:
        loxone.isEventsEnabled()
        loxone.app() == app
        1 * webSocket.sendCommand(Command.LOX_APP) >> {
            appCmdListener.onCommand(Command.LOX_APP, app)
        }
        1 * webSocket.sendCommand(Command.ENABLE_STATUS_UPDATE)
        1 * webSocket.registerWebSocketListener(*_)

        when:
        loxone.registerLoxoneAppListener(appListener)
        appCmdListener.onCommand(Command.LOX_APP, app)

        then:
        1 * appListener.onLoxoneApp(app)

        when:
        loxone.sendControlPulse(control)
        loxone.sendControlOn(control)
        loxone.sendControlOff(secControl)

        then:
        1 * webSocket.sendCommand({ cmd -> cmd.command ==~ /.*1177b172-020b-0b06-ffffc0f606ef595c\/Pulse/ })
        1 * webSocket.sendCommand({ cmd -> cmd.command ==~ /.*1177b172-020b-0b06-ffffc0f606ef595c\/On/ })
        1 * webSocket.sendSecureCommand({ cmd -> cmd.command ==~ /.*1177b172-020b-0b06-ffffc0f606ef595c\/Off/ })

        when:
        loxone.stop()

        then:
        1 * webSocket.close()
    }

    def "should create without visuPass"() {
        given:
        def loxone = new Loxone(new LoxoneEndpoint('localhost'), 'user', 'pass')

        when:
        loxone.auth().startVisuAuthentication()

        then:
        thrown(IllegalStateException)
    }

    def "should work with client miniservers"() {
        given:
        def loxone = new Loxone(new LoxoneEndpoint('localhost'), 'user', 'pass')
        def clientEndpoint = new LoxoneEndpoint('192.168.1.78')

        when:
        loxone.addClientMiniserver(clientEndpoint)

        then:
        loxone.allMiniserversHttp().size() == 2
        loxone.clientMiniserversHttp().size() == 1

        when:
        def clientHttp = loxone.clientMiniserverHttp(clientEndpoint)
        def clientHttp2 = loxone.clientMiniserverHttp(new LoxoneEndpoint('192.168.1.79'))

        then:
        loxone.clientMiniserversHttp().size() == 2
        clientHttp
        clientHttp2
    }

    def "should send custom built command"() {
        given:
        def control  = Stub(Control) {
            getUuid() >> new LoxoneUuid('1177b172-020b-0b06-ffffc0f606ef595c')
            isSecured() >> false
        }

        when:
        loxone.sendControlCommand(control) { genericControlCommand(it.uuid.toString(), 'customOp')}

        then:
        1 * webSocket.sendCommand({ it.command =~ /customOp/ })
    }
}
