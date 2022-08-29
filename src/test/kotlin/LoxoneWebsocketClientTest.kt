package cz.smarteon.loxone

import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should handle onClose`(remote: Boolean) {
        client.onClose(1000, "some reason", remote)

        expectCatching { webSocket.wsClosed() }.isSuccess()
        expectCatching { webSocket.connectionClosed(1000, remote) }.isSuccess()
        if (remote) expectCatching { webSocket.autoRestart() }.isSuccess()
    }
}
