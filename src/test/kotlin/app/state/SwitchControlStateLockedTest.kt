package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.app.SwitchControl
import cz.smarteon.loxone.message.TextEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class SwitchControlStateLockedTest {

    @Test
    fun `should not update locked when control has no locked state`() {
        val loxone = mockk<Loxone>()
        val control = mockk<SwitchControl> {
            every { stateLocked() } returns null
        }
        val state = SwitchControlState(loxone, control)

        // Precondition
        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()

        // Send any text event; since control has no locked state UUID, it must be ignored
        state.accept(
            TextEvent(
                LoxoneUuid("aaaaaaaa-bbbb-cccc-eeeeeeeeeeeeeeee"),
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                "{\"locked\": \"UI\", \"reason\": \"test\"}"
            )
        )

        // Still unchanged
        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()
    }

    @Test
    fun `should not update locked when event uuid does not match control's locked state uuid`() {
        val loxone = mockk<Loxone>()
        val lockedUuid = LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        val control = mockk<SwitchControl> {
            every { stateLocked() } returns lockedUuid
        }
        val state = SwitchControlState(loxone, control)

        // Precondition
        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()

        // Send text event with a different UUID than control.stateLocked()
        state.accept(
            TextEvent(
                LoxoneUuid("aaaaaaaa-bbbb-cccc-eeeeeeeeeeeeeeee"), // different from lockedUuid
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                "{\"locked\": \"UI\", \"reason\": \"no-op\"}"
            )
        )

        // Still unchanged
        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()
    }

    @Test
    fun `should update locked when control has locked state and event uuid matches`() {
        val loxone = mockk<Loxone>()
        val lockedUuid = LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        val control = mockk<SwitchControl> {
            every { stateLocked() } returns lockedUuid
        }
        val state = SwitchControlState(loxone, control)

        // Apply empty text first -> sets to NO
        state.accept(
            TextEvent(
                requireNotNull(control.stateLocked()),
                LoxoneUuid("bbbbbbbb-cccc-dddd-ffffffffffffffff"),
                ""
            )
        )
        expectThat(state.locked).isEqualTo(Locked.NO)
        expectThat(state.lockedReason).isNull()

        // Apply JSON -> sets to UI with reason
        state.accept(
            TextEvent(
                requireNotNull(control.stateLocked()),
                LoxoneUuid("bbbbbbbb-cccc-dddd-ffffffffffffffff"),
                "{\"locked\": \"UI\", \"reason\": \"via api\"}"
            )
        )
        expectThat(state.locked).isEqualTo(Locked.UI)
        expectThat(state.lockedReason).isEqualTo("via api")
    }
}
