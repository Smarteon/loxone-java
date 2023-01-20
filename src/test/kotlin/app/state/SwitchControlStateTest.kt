package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.LoxoneWebSocket
import cz.smarteon.loxone.app.SwitchControl
import cz.smarteon.loxone.message.ControlCommand
import cz.smarteon.loxone.message.TextEvent
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isSuccess

class SwitchControlStateTest {

    @Test
    fun `should process value event`() {
        val loxone = mockk<Loxone> {}
        val switchControl = mockk<SwitchControl> {
            every { stateActive() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val switchControlState = SwitchControlState(loxone, switchControl);

        expectThat(switchControlState.state).equals(null)
        switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 1.0)) // not for this control
        expectThat(switchControlState.state).equals(null)
        switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 1.0)) // for this control
        expectThat(switchControlState.state).equals(true)
        switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 0.0)) // not for this control
        expectThat(switchControlState.state).equals(true)
        switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 0.0)) // for this control
        expectThat(switchControlState.state).equals(false)
        switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 5.25)) // not for this control
        expectThrows<IllegalArgumentException> {
            switchControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 5.25)) // for this control
        }

    }

    @Test
    fun `should process text event`() {
        val loxone = mockk<Loxone> {}
        val switchControl = mockk<SwitchControl> {
            every { stateActive() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val switchControlState = SwitchControlState(loxone, switchControl);

        expectThat(switchControlState.locked).equals(null)
        switchControlState.accept(TextEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), "")) // not for this control
        expectThat(switchControlState.locked).equals(null)
        switchControlState.accept(TextEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e"), LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), "")) // for this control
        expectThat(switchControlState.locked).equals(Locked.NO)
        switchControlState.accept(TextEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), "{\"locked\": \"UI\", \"reason\": \"joske\"}")) // not for this control
        expectThat(switchControlState.locked).equals(Locked.NO)
        switchControlState.accept(TextEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e"), LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), "{\"locked\": \"UI\", \"reason\": \"joske\"}")) // for this control
        expectThat(switchControlState.locked).equals(Locked.UI)
    }

    @Test
    fun `should send update to loxone unsecured for state on`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val switchControl = mockk<SwitchControl> {
            every { isSecured } returns false
            every { uuid } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = SwitchControlState(loxone, switchControl);
        analogInfoControlState.stateOn();

        expectCatching { websocket.sendCommand(ControlCommand.genericControlCommand(switchControl.getUuid().toString(), "On")) }.isSuccess()
    }

    @Test
    fun `should send update to loxone unsecured for state off`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val switchControl = mockk<SwitchControl> {
            every { isSecured } returns false
            every { uuid } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = SwitchControlState(loxone, switchControl);
        analogInfoControlState.stateOff();

        expectCatching { websocket.sendCommand(ControlCommand.genericControlCommand(switchControl.getUuid().toString(), "Off")) }.isSuccess()
    }

    @Test
    fun `should send update to loxone secured for state on`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val switchControl = mockk<SwitchControl> {
            every { isSecured } returns true
            every { uuid } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = SwitchControlState(loxone, switchControl);
        analogInfoControlState.stateOn();

        expectCatching { websocket.sendSecureCommand(ControlCommand.genericControlCommand(switchControl.getUuid().toString(), "On")) }.isSuccess()
    }

    @Test
    fun `should send update to loxone secured for state off`() {
        val websocket = mockk<LoxoneWebSocket>(relaxed = true)
        val loxone = mockk<Loxone> {
            every { webSocket() } returns websocket
        }
        val switchControl = mockk<SwitchControl> {
            every { isSecured } returns true
            every { uuid } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = SwitchControlState(loxone, switchControl);
        analogInfoControlState.stateOff();

        expectCatching { websocket.sendSecureCommand(ControlCommand.genericControlCommand(switchControl.getUuid().toString(), "Off")) }.isSuccess()
    }
}