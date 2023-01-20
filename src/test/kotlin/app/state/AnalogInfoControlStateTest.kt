package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.LoxoneWebSocket
import cz.smarteon.loxone.app.AnalogInfoControl
import cz.smarteon.loxone.message.ControlCommand
import cz.smarteon.loxone.message.TextEvent
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isSuccess


class AnalogInfoControlStateTest {

    @Test
    fun `should process value event`() {
        val loxone = mockk<Loxone> {}
        val analogInfoControl = mockk<AnalogInfoControl> {
            every { stateValue() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = AnalogInfoControlState(loxone, analogInfoControl);

        expectThat(analogInfoControlState.value).equals(null)
        analogInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 27.27)) // not for this control
        expectThat(analogInfoControlState.value).equals(null)
        analogInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 25.25)) // for this control
        expectThat(analogInfoControlState.value).equals(25.25)
        analogInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 27.27)) // not for this control
        expectThat(analogInfoControlState.value).equals(25.25)
    }

    @Test
    fun `should not process text event`() {
        val loxone = mockk<Loxone> {}
        val analogInfoControl = mockk<AnalogInfoControl> {
            every { stateValue() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = AnalogInfoControlState(loxone, analogInfoControl);

        expectThat(analogInfoControlState.value).equals(null)
        analogInfoControlState.accept(TextEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), "value"))
        expectThat(analogInfoControlState.value).equals(null)
    }
}
