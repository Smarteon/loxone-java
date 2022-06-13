package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*
import kotlin.reflect.KClass

class ControlTest {

    @ParameterizedTest(name = "should deserialize {0}Control")
    @EnumSource(TestControl::class)
    fun `should deserialize`(testControl: TestControl) {
        expectThat(readResource("app/${testControl.resourceName}.json", Control::class)) {
            get { this::class }.isEqualTo(testControl.type)
            get { name }.isEqualTo(testControl.controlName)
            get { states.size }.isEqualTo(testControl.statesSize)
            get { secured }.isEqualTo(testControl.secured)
            get { type }.isEqualTo(testControl.controlType)
            get { rating }.isEqualTo(testControl.controlRating)
            if (testControl.specificState != null) {
                get { getStates()?.get(testControl.specificState.first)?.only() }
                    .isEqualTo(testControl.specificState.second)
            }

        }
    }

    enum class TestControl(
        val type: KClass<out Control>,
        val controlName: String,
        val statesSize: Int,
        val secured: Boolean = false,
        val specificState: Pair<String, LoxoneUuid>? = null,
        val controlType: String,
        val controlRating: Int


    ) {
        Alarm(AlarmControl::class, "Alarm", 10, true, Pair("armed", LoxoneUuid("0f86a2fe-0378-3e08-ffffb2d4efc8b5b6"))),
        Switch(SwitchControl::class, "CallState", 1, false, Pair("active", LoxoneUuid("110cb849-0125-20f9-ffffac0ced78bcf2"))),
        Presence(PresenceControl::class, "PresTest", 0),
        DigitalInfo(DigitalInfoControl::class, "kitchen-SqueezeSound", 1),
        AnalogInfo(AnalogInfoControl::class, "kitchen-SqueezeTrack", 2);

        val resourceName = type.simpleName!!.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
}
