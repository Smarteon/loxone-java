package cz.smarteon.loxone

import cz.smarteon.loxone.message.ControlCommand
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.java_websocket.client.WebSocketClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.lang.Thread.sleep
import java.security.Security
import java.util.concurrent.ScheduledExecutorService

class LoxoneWebSocketTest {
    private lateinit var loxoneWebSocket: LoxoneWebSocket
    private lateinit var authMock: LoxoneAuth
    private lateinit var wsClientMock: WebSocketClient
    private val authListener: AuthListener
        get() = authListenerSlot.captured
    private val authListenerSlot = slot<AuthListener>()
    private val captureRefreshScheduler = slot<ScheduledExecutorService>()
    private val scheduler: ScheduledExecutorService by lazy { captureRefreshScheduler.captured }

    @BeforeAll
    fun setupSpec() {
        Security.addProvider(BouncyCastleProvider())
    }

    @BeforeEach
    fun setup() {
        authMock = mockk(relaxed = true) {
            every { registerAuthListener(capture(authListenerSlot)) } just Runs
            every { setAutoRefreshScheduler(capture(captureRefreshScheduler)) } just Runs
        }
        wsClientMock = mockk(relaxed = true)
        loxoneWebSocket = LoxoneWebSocket(LoxoneEndpoint("localhost", 12345), authMock) { _, _ -> wsClientMock }
        loxoneWebSocket.authTimeoutSeconds = 1
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
        loxoneWebSocket.close()
    }

    @Test
    fun `should set loxone auth interop`() {
        verify { authMock.registerAuthListener(isNull(inverse = true)) }
        verify { authMock.setAutoRefreshScheduler(isNull(inverse = true)) }

        /* mockk cannot instantiate private interface
        verify { authMock.setCommandSender(isNull(inverse=true)) } */
    }

    @Test
    fun `should auto restart`() {
        loxoneWebSocket.isAutoRestart = true
        loxoneWebSocket.retries = 0
        loxoneWebSocket.authTimeoutSeconds = 1

        every { authMock.isInitialized } returns true
        every { wsClientMock.connect() } answers { loxoneWebSocket.connectionOpened() }
        every { authMock.startAuthentication() } answers { authListener.authCompleted() }
        loxoneWebSocket.sendCommand(Command.LOX_APP)

        expectThat(loxoneWebSocket) {
            get { retries }.isEqualTo(0)
            get { authTimeoutSeconds }.isEqualTo(1)
        }
        verify(exactly = 1) { authMock.isInitialized }

        loxoneWebSocket.autoRestart()
        sleep(2100)

        verify(exactly = 2) { authMock.isInitialized }
    }

    @Test
    fun `should call websocket listener open`() {
        val listener = mockk<LoxoneWebSocketListener>(relaxed = true)

        loxoneWebSocket.registerWebSocketListener(listener)
        loxoneWebSocket.connectionOpened()
        sleep(10)

        verify(exactly = 1) { listener.webSocketOpened() }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should call websocket listener closed`(remote: Boolean) {
        val listener = mockk<LoxoneWebSocketListener>(relaxed = true)

        loxoneWebSocket.registerWebSocketListener(listener)
        loxoneWebSocket.connectionClosed(1000, remote)
        sleep(10)

        if (remote) {
            verify(exactly = 1) { listener.webSocketRemoteClosed(1000) }
        } else {
            verify(exactly = 1) { listener.webSocketLocalClosed(1000) }
        }
    }

    @Test
    fun `should close properly`() {
        loxoneWebSocket.retries = 0
        every { wsClientMock.connect() } answers { loxoneWebSocket.connectionOpened() }
        every { authMock.startAuthentication() } answers { authListener.authCompleted() }

        loxoneWebSocket.sendCommand(Command.LOX_APP)
        loxoneWebSocket.close()

        expectThat(loxoneWebSocket.scheduler.isShutdown).isTrue()
        verify(exactly = 1) { wsClientMock.closeBlocking() }
    }

    @Test
    fun `should close properly when ws interrupted`() {
        loxoneWebSocket.retries = 0
        every { wsClientMock.connect() } answers { loxoneWebSocket.connectionOpened() }
        every { authMock.startAuthentication() } answers { authListener.authCompleted() }
        every { wsClientMock.closeBlocking() } throws InterruptedException("Testing Interrupt")

        expectThrows<LoxoneException> {
            loxoneWebSocket.sendCommand(Command.LOX_APP)
            loxoneWebSocket.close()
        }
        expectThat(loxoneWebSocket.scheduler.isShutdown).isTrue()
    }

    @Test
    fun `should not send secure command when visuPass not set`() {
        loxoneWebSocket.retries = 0
        every { wsClientMock.connect() } answers { loxoneWebSocket.connectionOpened() }
        every { authMock.startAuthentication() } answers { authListener.authCompleted() }
        every { authMock.startVisuAuthentication() } throws IllegalStateException("Can't compute visuHash")

        expectThrows<LoxoneException> {
            loxoneWebSocket.sendSecureCommand(ControlCommand.genericControlCommand("uuid", "operation"))
        }
    }
}
