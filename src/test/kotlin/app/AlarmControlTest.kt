package cz.smarteon.loxone.app

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class AlarmControlTest {
    @Test
    fun `should deserialize`() {
        expectThat(readResource("app/alarmControl.json", AlarmControl::class)) {
            get { uuid }.isLoxoneUuid("0f86a2fe-0378-3e15-ffff373f9870b52a")
            get { name }.isEqualTo("Alarm")
            get { secured }.isTrue()
            get { stateArmed() }.isLoxoneUuid("0f86a2fe-0378-3e08-ffffb2d4efc8b5b6")
            get { stateArmedDelay() }.isLoxoneUuid("0f86a2fe-0378-3e11-ffffb2d4efc8b5b6")
            get { stateArmedDelayTotal() }.isLoxoneUuid("0f86a2fe-0378-3dfb-ffffb2d4efc8b5b6")
            get { stateLevel() }.isLoxoneUuid("10a73e3b-01c5-1902-ffff373f9870b52a")
            get { stateNextLevel() }.isLoxoneUuid("10a73e3b-01c5-18ff-ffff373f9870b52a")
            get { stateNextLevelDelay() }.isLoxoneUuid("10a73e3b-01c5-1900-ffff373f9870b52a")
            get { stateNextLevelDelayTotal() }.isLoxoneUuid("10a73e3b-01c5-1901-ffff373f9870b52a")
            get { stateDisabledMove() }.isLoxoneUuid("10a73e3b-01c5-1904-ffff373f9870b52a")
            get { stateSensors() }.isLoxoneUuid("0f86a2fe-0378-3e15-ffff373f9870b52a")
            get { stateStartTime() }.isLoxoneUuid("10a73e3b-01c5-1903-ffff373f9870b52a")
        }
    }
}
