package cz.smarteon.loxone

import cz.smarteon.loxone.app.Control
import cz.smarteon.loxone.app.LoxoneApp
import cz.smarteon.loxone.message.ControlCommand.genericControlCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import nl.jqno.equalsverifier.internal.lib.bytebuddy.build.HashCodeAndEqualsPlugin.ValueHandling
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo
import strikt.assertions.isTrue

class LoxoneTest {
    private lateinit var loxone: Loxone
    private lateinit var auth: LoxoneAuth
    private lateinit var webSocket: LoxoneWebSocket
    private lateinit var http: LoxoneHttp
    private lateinit var appCmdListener: CommandResponseListener<Any?>

    @BeforeEach
    fun setup() {
        http = mockk()
        webSocket = mockk(relaxed = true) {
            val captureObject = slot<CommandResponseListener<Any?>>()
            every { registerListener(capture(captureObject)) } answers { appCmdListener = captureObject.captured }
        }
        auth = mockk(relaxed = true)

        loxone = Loxone(http, webSocket, auth)
    }

    @Test
    fun `should initialize`() {
        expectThat(loxone) {
            get { auth() }.isEqualTo(auth)
            get { webSocket() }.isEqualTo(webSocket)
            get { http() }.isEqualTo(http)
            get { allMiniserversHttp().count() }.isEqualTo(1)
            get { clientMiniserversHttp().none() }.isTrue()
            get { appCmdListener }.isNotEqualTo(null)
        }
    }

    @Test
    fun `test basic flow`() {
        val app = mockk<LoxoneApp>()
        val appListener = mockk<LoxoneAppListener>(relaxed = true)
        val control = mockk<Control> {
            every { uuid } returns LoxoneUuid("1177b172-020b-0b06-ffffc0f606ef595c")
            every { isSecured } returns false
        }
        val secControl = mockk<Control> {
            every { uuid } returns LoxoneUuid("1177b172-020b-0b06-ffffc0f606ef595c")
            every { isSecured } returns true
        }
        every { webSocket.sendCommand(Command.LOX_APP) } answers { appCmdListener.onCommand(Command.LOX_APP, app) }

        loxone.isEventsEnabled = true
        loxone.start()
        expectThat(loxone) {
            get { isEventsEnabled }.isTrue()
            get { app() }.isEqualTo(app)
        }
        verify(exactly = 1) { webSocket.sendCommand(Command.LOX_APP) }
        verify(exactly = 1) { webSocket.sendCommand(Command.ENABLE_STATUS_UPDATE) }
        verify(exactly = 1) { webSocket.registerWebSocketListener(any()) }

        loxone.registerLoxoneAppListener(appListener)
        appCmdListener.onCommand(Command.LOX_APP, app)
        verify(exactly = 1) { appListener.onLoxoneApp(app) }

        loxone.sendControlPulse(control)
        loxone.sendControlOn(control)
        loxone.sendControlOff(secControl)
        verify(exactly = 1) { webSocket.sendCommand(match { Regex(".*1177b172-020b-0b06-ffffc0f606ef595c/Pulse").matches(it.command) }) }
        verify(exactly = 1) { webSocket.sendCommand(match { Regex(".*1177b172-020b-0b06-ffffc0f606ef595c/On").matches(it.command) }) }
        verify(exactly = 1) { webSocket.sendSecureCommand(match { Regex(".*1177b172-020b-0b06-ffffc0f606ef595c/Off").matches(it.command) }) }

        loxone.stop()
        verify(exactly = 1) { webSocket.close() }
    }

    @Test
    fun `test create without visuPass`() {
        val loxone = Loxone(LoxoneEndpoint("localhost"), "user", "pass")

        expectThrows<IllegalStateException> { loxone.auth().startVisuAuthentication() }
    }

    @Test
    fun `should work with client miniservers`() {
        val loxone = Loxone(LoxoneEndpoint("localhost"), "user", "pass")
        val clientEndpoint = LoxoneEndpoint("192.168.1.78")

        loxone.addClientMiniserver(clientEndpoint)
        expectThat(loxone) {
            get { allMiniserversHttp().count() }.isEqualTo(2)
            get { clientMiniserversHttp().count() }.isEqualTo(1)
        }

        loxone.clientMiniserverHttp(clientEndpoint)
        loxone.clientMiniserverHttp(LoxoneEndpoint("192.168.1.79"))
        expectThat(loxone) {
            get { clientMiniserversHttp().count() }.isEqualTo(2)
        }
    }

    @Test
    fun `should send custom build command`() {
        val control = mockk<Control> {
            every { uuid } returns LoxoneUuid("1177b172-020b-0b06-ffffc0f606ef595c")
            every { isSecured } returns false
        }

        loxone.sendControlCommand(control) { genericControlCommand(it.uuid.toString(), "customOp") }
        verify(exactly = 1) { webSocket.sendCommand(match { it.command.matches(Regex(".*customOp")) }) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should stop`(killToken: Boolean) {
        loxone.stop(killToken)
        verify(exactly = if (killToken) 1 else 0) { auth.killToken() }
        verify { webSocket.close() }
    }
}
