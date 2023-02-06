package cz.smarteon.loxone.app.state

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneEventListener
import cz.smarteon.loxone.app.LoxoneApp
import cz.smarteon.loxone.app.SwitchControl
import cz.smarteon.loxone.message.ValueEvent
import cz.smarteon.loxone.readResource
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.all
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo
import strikt.assertions.isNotNull

class LoxoneStateTest {
    private lateinit var eventListener: LoxoneEventListener
    private lateinit var loxone: Loxone
    private lateinit var app: LoxoneApp
    private lateinit var state: LoxoneState

    @BeforeEach
    fun setup() {
        loxone = mockk(relaxed = true) {
            val captureObject = slot<LoxoneEventListener>()
            every { webSocket().registerListener(capture(captureObject)) } answers {
                eventListener = captureObject.captured
            }
        }
        app = readResource("app/LoxAppSwitch.json")
        state = LoxoneState(loxone)
    }

    @Test
    fun `controlStates should be compatible`() {
        expectThat(state.supportedControlsStateMap.entries).all {
            get { value.getDeclaredConstructor(loxone.javaClass, key) }.isNotNull()
        }
    }

    @Test
    fun `should initialize`() {
        val switchControl = app.getControl(SwitchControl::class.java)
        state.onLoxoneApp(app)

        expectThat(state) {
            get { controlStates }.hasSize(1)
            get { eventListener }.isNotEqualTo(null)
            get { getStateForControl<SwitchControlState>(switchControl) }.isNotNull()
        }
    }

    @Test
    fun `should process value event`() {
        val switchControl = app.getControl(SwitchControl::class.java)
        state.onLoxoneApp(app)
        val switchControlState = state.getStateForControl<SwitchControlState>(switchControl)

        expectThat(switchControlState!!.state).isEqualTo(null)

        state.onEvent(ValueEvent(switchControl!!.stateActive(), 1.0))
        expectThat(switchControlState!!.state).isEqualTo(true)
        state.onEvent(ValueEvent(switchControl!!.stateActive(), 0.0))
        expectThat(switchControlState!!.state).isEqualTo(false)
    }

    @Test
    fun `should throw exception when loxone is already started but events are not enabled`() {
        loxone = mockk(relaxed = true) {
            every { isStarted } answers { true }
            every { isEventsEnabled} answers { false }
        }
        expectThrows<IllegalArgumentException> { LoxoneState(loxone) }
    }
}
