package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryPeriodTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/period.json"))
            .isA<CalEntryPeriod>().and {
                get { uuid }.isLoxoneUuid("1b6f480b-0393-9b59-ffff504f94a03d3e")
                get { name }.isEqualTo("Period test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(3)
                get { startYear }.isEqualTo(2024)
                get { startMonth }.isEqualTo(8)
                get { startDay }.isEqualTo(3)
                get { endYear }.isEqualTo(2024)
                get { endMonth }.isEqualTo(8)
                get { endDay }.isEqualTo(4)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Period test/1/3/2024/8/3/2024/8/4")
                get { updateEntryCommand(this.uuid).command }.isEqualTo("jdev/sps/calendarupdateentry/1b6f480b-0393-9b59-ffff504f94a03d3e/Period test/1/3/2024/8/3/2024/8/4")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntryPeriod("Period test", 1, 2024, 8, 3, 2024, 8, 4),
            readResource<CalEntryBase>("calendar/periodNoUuid.json")
        )
    }
}
