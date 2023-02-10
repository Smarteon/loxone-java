package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.app.DigitalInfoControl
import cz.smarteon.loxone.message.TextEvent
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class DigitalInfoControlStateTest {

    enum class DigitalInfoTestValues(
        val uuid: String,
        val startingValue: Double?,
        val newValue: Double,
        val expectedValue: Boolean?
    ) {
        WrongControl("0f869a64-028d-0cc2-ffffd4c75dbaf53d", 0.0, 1.0, false),
        WrongControlNull("0f869a64-028d-0cc2-ffffd4c75dbaf53d", null, 1.0, null),
        CorrectControlNull("0f869a64-028d-0cc2-ffffd4c75dbaf53c", null, 0.0, false),
        CorrectControl("0f869a64-028d-0cc2-ffffd4c75dbaf53c", 0.0, 1.0, true)
    }

    @ParameterizedTest
    @EnumSource(DigitalInfoTestValues::class)
    fun `should process value event`(testParams: DigitalInfoTestValues) {
        val loxone = mockk<Loxone> {}
        val digitalInfoControl = mockk<DigitalInfoControl> {
            every { stateActive() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val digitalInfoControlState = DigitalInfoControlState(loxone, digitalInfoControl);

        expectThat(digitalInfoControlState) {
            get { state }.isNull()
            testParams.startingValue?.let {
                digitalInfoControlState.accept(ValueEvent(digitalInfoControl.stateActive(), testParams.startingValue))
            }
            digitalInfoControlState.accept(ValueEvent(LoxoneUuid(testParams.uuid), testParams.newValue))
            get { state }.isEqualTo(testParams.expectedValue)
        }
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
