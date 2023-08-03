package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CalEntryBaseTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/base.json")){
            get { uuid }.isLoxoneUuid("1b6f49d6-030f-9bc2-ffff504f94a03d3e")
            get { name }.isEqualTo("base test")
            get { operatingMode }.isEqualTo(1)
            get { calMode }.isEqualTo(5)
            get { deleteEntryCommand().command }.isEqualTo("jdev/sps/calendardeleteentry/1b6f49d6-030f-9bc2-ffff504f94a03d3e")
        }
    }
}
