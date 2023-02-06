package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.app.AnalogInfoControl
import cz.smarteon.loxone.app.DigitalInfoControl
import cz.smarteon.loxone.message.TextEvent
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows

class DigitalInfoControlStateTest {

    @Test
    fun `should process value event`() {
        val loxone = mockk<Loxone> {}
        val digitalInfoControl = mockk<DigitalInfoControl> {
            every { stateActive() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val digitalInfoControlState = DigitalInfoControlState(loxone, digitalInfoControl);

        expectThat(digitalInfoControlState.state).equals(null)
        digitalInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 1.0)) // not for this control
        expectThat(digitalInfoControlState.state).equals(null)
        digitalInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 0.0)) // for this control
        expectThat(digitalInfoControlState.state).equals(false)
        digitalInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"), 1.0)) // not for this control
        expectThat(digitalInfoControlState.state).equals(false)
        digitalInfoControlState.accept(ValueEvent(LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c"), 1.0)) // for this control
        expectThat(digitalInfoControlState.state).equals(true)
    }

    @Test
    fun `should not process text event`() {
        val loxone = mockk<Loxone> {}
        val digitalInfoControl = mockk<DigitalInfoControl> {
            every { stateActive() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val digitalInfoControlState = DigitalInfoControlState(loxone, digitalInfoControl);

        expectThat(digitalInfoControlState.state).isNull()
        digitalInfoControlState.accept(
            TextEvent(
                LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e"),
                LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53d"),
                "value"
            )
        )
        expectThat(digitalInfoControlState.state).isNull()
    }
}
