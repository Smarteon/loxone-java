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

class LockableControlStateTest {

    @Test
    fun `should set locked NO on empty text event`() {
        val loxone = mockk<Loxone>()
        val switchControl = mockk<SwitchControl> {
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val state = SwitchControlState(loxone, switchControl)

        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()

        state.accept(
            TextEvent(
                requireNotNull(switchControl.stateLocked()),
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                ""
            )
        )

        expectThat(state.locked).isEqualTo(Locked.NO)
        expectThat(state.lockedReason).isNull()
    }

    @Test
    fun `should parse locked JSON and update state`() {
        val loxone = mockk<Loxone>()
        val switchControl = mockk<SwitchControl> {
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val state = SwitchControlState(loxone, switchControl)

        state.accept(
            TextEvent(
                requireNotNull(switchControl.stateLocked()),
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                "{" +
                    "\"locked\": \"UI\", " +
                    "\"reason\": \"manual lock\"" +
                    "}"
            )
        )

        expectThat(state.locked).isEqualTo(Locked.UI)
        expectThat(state.lockedReason).isEqualTo("manual lock")
    }

    @Test
    fun `should ignore unrelated text events`() {
        val loxone = mockk<Loxone>()
        val switchControl = mockk<SwitchControl> {
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val state = SwitchControlState(loxone, switchControl)

        state.accept(
            TextEvent(
                LoxoneUuid("99999999-9999-9999-9999999999999999"),
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                "{\"locked\": \"UI\"}"
            )
        )

        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()
    }

    @Test
    fun `should handle malformed JSON without changing state`() {
        val loxone = mockk<Loxone>()
        val switchControl = mockk<SwitchControl> {
            every { stateLocked() } returns LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53e")
        }
        val state = SwitchControlState(loxone, switchControl)

        state.accept(
            TextEvent(
                requireNotNull(switchControl.stateLocked()),
                LoxoneUuid("11111111-1111-1111-1111111111111111"),
                "{not a json"
            )
        )

        expectThat(state.locked).isNull()
        expectThat(state.lockedReason).isNull()
    }
}
