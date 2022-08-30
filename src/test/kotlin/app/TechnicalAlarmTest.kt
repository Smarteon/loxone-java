package cz.smarteon.loxone.app

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse

class TechnicalAlarmTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<TechnicalAlarmControl>("app/technicalAlarmControl.json")) {
            get { uuid }.isLoxoneUuid("10a75401-013a-1272-fffff8bbb0459c02")
            get { name }.isEqualTo("Technical alarm")
            get { secured }.isFalse()
            get { stateLevel() }.isLoxoneUuid("1351c8fb-0264-7ef4-fffff8bbb0459c02")
            get { stateNextLevel() }.isLoxoneUuid("1351c8fb-0264-7ef5-fffff8bbb0459c02")
            get { stateNextLevelDelay() }.isLoxoneUuid("1351c8fb-0264-7ef6-fffff8bbb0459c02")
            get { stateNextLevelDelayTotal() }.isLoxoneUuid("1351c8fb-0264-7ef7-fffff8bbb0459c02")
            get { stateSensors() }.isLoxoneUuid("10a75401-013a-1272-fffff8bbb0459c02")
            get { stateStartTime() }.isLoxoneUuid("1351c8fb-0264-7efa-fffff8bbb0459c02")
            get { stateAlarmCause() }.isLoxoneUuid("1351c8fb-0264-7ef9-fffff8bbb0459c02")
            get { stateAcousticAlarm() }.isLoxoneUuid("10a75401-013a-126e-ffff9084f3b66cb8")
            get { stateTestAlarm() }.isLoxoneUuid("10a75401-013a-1270-ffff9084f3b66cb8")
            get { stateTimeServiceMode() }.isLoxoneUuid("1351c8fb-0264-7ef8-fffff8bbb0459c02")
            get { stateAreAlarmSignalsOff() }.isLoxoneUuid("1351c8fb-0264-7efb-fffff8bbb0459c02")
        }
    }
}
