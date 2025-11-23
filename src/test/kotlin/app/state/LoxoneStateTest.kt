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
import strikt.api.expect
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.all
import strikt.assertions.hasSize
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue

class LoxoneStateTest {
    private lateinit var eventListener: LoxoneEventListener
    private lateinit var loxone: Loxone
    private lateinit var app: LoxoneApp
    private lateinit var loxoneState: LoxoneState

    @BeforeEach
    fun setup() {
        loxone = mockk(relaxed = true) {
            val captureObject = slot<LoxoneEventListener>()
            every { webSocket().registerListener(capture(captureObject)) } answers {
                eventListener = captureObject.captured
            }
        }
        app = readResource("app/LoxAppSwitch.json")
        loxoneState = LoxoneState(loxone)
    }

    @Test
    fun `controlStates should be compatible`() {
        expectThat(loxoneState.supportedControlsStateMap.entries).all {
            get { value.getDeclaredConstructor(Loxone::class.java, key) }.isNotNull()
        }
    }

    @Test
    fun `should initialize`() {
        val switchControl = app.getControl(SwitchControl::class.java)
        loxoneState.onLoxoneApp(app)

        expect {
            that(loxoneState.controlStates).hasSize(1)
            that(switchControl).isNotNull().and {
                get { loxoneState.getStateForControl<SwitchControlState>(this) }.isNotNull()
            }
        }
    }

    @Test
    fun `should process value event`() {
        val switchControl = app.getControl(SwitchControl::class.java)
        loxoneState.onLoxoneApp(app)

        expectThat(switchControl).isNotNull().and {
            val activeStateUuid = subject.stateActive()
            get { loxoneState.getStateForControl<SwitchControlState>(this) }.isNotNull().and {
                get { state }.isNull()
                loxoneState.onEvent(ValueEvent(activeStateUuid, 1.0))
                get { state }.isTrue()
                loxoneState.onEvent(ValueEvent(activeStateUuid, 0.0))
                get { state }.isFalse()
            }
        }
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
