package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneWebSocket
import cz.smarteon.loxone.app.AnalogInfoControl
import cz.smarteon.loxone.message.ControlCommand
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isSuccess


class ControlStateTest {
    @Test
    fun `should send update to loxone unsecured`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val analogInfoControl = mockk<AnalogInfoControl> {
            every { isSecured } returns false
        }
        val analogInfoControlState = AnalogInfoControlState(loxone, analogInfoControl);
        val analogInfoControlCommand = ControlCommand.genericControlCommand("uuid", "op")

        analogInfoControlState.sendCommand(analogInfoControlCommand)

        expectCatching { websocket.sendCommand(analogInfoControlCommand) }.isSuccess()
    }

    @Test
    fun `should send update to loxone secured`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val analogInfoControl = mockk<AnalogInfoControl> {
            every { isSecured } returns true
        }
        val analogInfoControlState = AnalogInfoControlState(loxone, analogInfoControl);
        val analogInfoControlCommand = ControlCommand.genericControlCommand("uuid", "op")

        analogInfoControlState.sendCommand(analogInfoControlCommand)

        expectCatching { websocket.sendSecureCommand(analogInfoControlCommand) }.isSuccess()
    }
}
