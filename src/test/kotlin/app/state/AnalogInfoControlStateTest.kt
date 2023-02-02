package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.app.AnalogInfoControl
import cz.smarteon.loxone.message.TextEvent
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo


class AnalogInfoControlStateTest {

    enum class AnalogInfoTestValues(
        val uuid: String,
        val startingValue: Double?,
        val newValue: Double,
        val expectedValue: Double?
    ) {
        CorrectControl("0f869a64-028d-0cc2-ffffd4c75dbaf53c", null, 25.25, 25.25),
        WrongControlCase1("0f869a64-028d-0cc2-ffffd4c75dbaf53d", null, 27.27, null),
        WrongControlCase2("0f869a64-028d-0cc2-ffffd4c75dbaf53d",25.25, 27.27, 25.25)
    }
    @ParameterizedTest
    @EnumSource(AnalogInfoTestValues::class)
    fun `should process value event`(testParams: AnalogInfoTestValues) {
        val loxone = mockk<Loxone> {}
        val analogInfoControl = mockk<AnalogInfoControl> {
            every { stateValue() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        }
        val analogInfoControlState = AnalogInfoControlState(loxone, analogInfoControl)

        expectThat(analogInfoControlState) {
            get { value }.isEqualTo(null)
            testParams.startingValue?.let {
                analogInfoControlState.accept(ValueEvent(analogInfoControl.stateValue(), testParams.startingValue))
            }
            analogInfoControlState.accept(ValueEvent(LoxoneUuid(testParams.uuid), testParams.newValue))
            get { value }.isEqualTo(testParams.expectedValue)
        }
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
