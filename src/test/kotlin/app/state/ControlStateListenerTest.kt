package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.app.DigitalInfoControl
import cz.smarteon.loxone.message.ValueEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ControlStateListenerTest {

    @Test
    fun `listener notified only on state change and respects unregister`() {
        val loxone = mockk<Loxone>()
        val control = mockk<DigitalInfoControl> {
            every { stateActive() } returns LoxoneUuid("11111111-1111-1111-1111111111111111")
        }
        val state = DigitalInfoControlState(loxone, control)

        var notifications = 0
        val listener = object : ControlStateListener<Boolean, DigitalInfoControl> {
            override fun onStateChange(controlState: ControlState<Boolean, DigitalInfoControl>) {
                notifications++
            }
        }

        state.registerListener(listener)

        // Unrelated UUID -> no notify
        state.accept(ValueEvent(LoxoneUuid("11111111-1111-1111-11111111111111FF"), 1.0))
        expectThat(notifications).isEqualTo(0)

        // First change from null -> true -> notify
        state.accept(ValueEvent(control.stateActive(), 1.0))
        expectThat(notifications).isEqualTo(1)

        // Same value again -> no state change -> no notify
        state.accept(ValueEvent(control.stateActive(), 1.0))
        expectThat(notifications).isEqualTo(1)

        // Value change true -> false -> notify
        state.accept(ValueEvent(control.stateActive(), 0.0))
        expectThat(notifications).isEqualTo(2)

        // Unregister and change again -> no further notifications
        state.unregisterListener(listener)
        state.accept(ValueEvent(control.stateActive(), 1.0))
        expectThat(notifications).isEqualTo(2)
    }
}
