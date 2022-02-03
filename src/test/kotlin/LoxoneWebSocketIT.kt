package cz.smarteon.loxone

import cz.smarteon.loxone.Command.voidWsCommand
import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.app.MiniserverType.KNOWN
import cz.smarteon.loxone.message.ControlCommand.genericControlCommand
import cz.smarteon.loxone.mock.CryptoMock.PASS
import cz.smarteon.loxone.mock.CryptoMock.USER
import cz.smarteon.loxone.mock.CryptoMock.VISU_PASS
import cz.smarteon.loxone.mock.MockMiniserver
import org.awaitility.kotlin.await
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThrows
import java.security.Security

class LoxoneWebSocketIT {

    private val mockServer = MockMiniserver().apply { start() }

    private lateinit var lws: LoxoneWebSocket

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @BeforeEach
    fun setup() {
        val endpoint = LoxoneEndpoint("localhost", mockServer.port)
        lws = LoxoneWebSocket(endpoint, LoxoneAuth(LoxoneHttp(endpoint), USER, PASS, VISU_PASS))
        lws.authTimeoutSeconds = 1
    }

    @Test
    fun `should send simple command`() {
        mockServer.expect(equalTo("testCmd"))
        lws.sendCommand(Command.voidWsCommand(MiniserverType.KNOWN, "testCmd"))
        await.until { mockServer.verifyExpectations() }
    }

    @Test
    fun `should send secure command`() {
        mockServer.expect(equalTo("testUuid/pulse"))
        lws.sendSecureCommand(genericControlCommand("testUuid", "pulse"))
        await.until { mockServer.verifyExpectations() }
    }

    @Test
    fun `should handle bad credentials`() {
        lws.retries = 0
        mockServer.badCredentials = 1
        expectThrows<LoxoneException> { lws.sendCommand(voidWsCommand(KNOWN, "baf")) }
    }

    @Test
    fun `should retry on bad credentials`() {
        lws.retries = 5
        mockServer.badCredentials = 4
        mockServer.expect(equalTo("baf"))
        lws.sendCommand(voidWsCommand(KNOWN, "baf"))
        await.until { mockServer.verifyExpectations() }
    }

    @Test
    fun `should handle server restart`() {
        mockServer.expect(equalTo("beforeRestart"))
        lws.sendCommand(voidWsCommand(KNOWN, "beforeRestart"))
        await.until { mockServer.verifyExpectations() }

        mockServer.restart()

        mockServer.expect(equalTo("afterRestart"))
        lws.sendCommand(voidWsCommand(KNOWN, "afterRestart"))
        await.until { mockServer.verifyExpectations() }
    }

    @AfterEach
    fun tearDown() {
        lws.close()
    }

    @AfterAll
    fun stopMockServer() {
        mockServer.stop()
    }
}
