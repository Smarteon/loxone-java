package cz.smarteon.loxone

import io.mockk.mockk
import io.mockk.verify
import org.java_websocket.framing.CloseFrame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isSuccess
import java.net.URI
import java.nio.ByteBuffer

class LoxoneWebsocketClientTest {
    private lateinit var client: LoxoneWebsocketClient
    private lateinit var webSocket: LoxoneWebSocket

    @BeforeEach
    fun setup() {
        webSocket = mockk<LoxoneWebSocket>(relaxed = true)
        client = LoxoneWebsocketClient(webSocket, URI.create(""))
    }

    @Test
    fun `should parse binary message`() {
        expectCatching {
            client.onMessage(ByteBuffer.wrap(byteArrayOf(0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0)))
        }.isSuccess()
    }

    @Test
    fun `should auto-restart on remote close`() {
        client.onClose(CloseFrame.NORMAL, "normally closed", true)

        verify { webSocket.connectionClosed(CloseFrame.NORMAL, true) }
        verify { webSocket.autoRestart() }
    }

    @Test
    fun `should auto-restart on abnormal local close (lost connection or pong timeout)`() {
        client.onClose(CloseFrame.ABNORMAL_CLOSE, "no pong in time", false)

        verify { webSocket.connectionClosed(CloseFrame.ABNORMAL_CLOSE, false) }
        verify { webSocket.autoRestart() }
    }

    @Test
    fun `should not auto-restart on deliberate local close`() {
        client.onClose(CloseFrame.NORMAL, "closed by client", false)

        verify { webSocket.connectionClosed(CloseFrame.NORMAL, false) }
        verify(exactly = 0) { webSocket.autoRestart() }
    }
}
