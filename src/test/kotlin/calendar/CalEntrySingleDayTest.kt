package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntrySingleDayTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/singleDay.json"))
            .isA<CalEntrySingleDay>().and {
                get { uuid }.isLoxoneUuid("1b6f47f0-03b0-9b3d-ffff504f94a03d3e")
                get { name }.isEqualTo("Single day test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(2)
                get { startYear }.isEqualTo(2024)
                get { startMonth }.isEqualTo(8)
                get { startDay }.isEqualTo(3)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Single day test/1/2/2024/8/3")
                get { updateEntryCommand(this.uuid).command }.isEqualTo("jdev/sps/calendarupdateentry/1b6f47f0-03b0-9b3d-ffff504f94a03d3e/Single day test/1/2/2024/8/3")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntrySingleDay("Single day test", 1, 2024, 8, 3),
            readResource<CalEntryBase>("calendar/singleDayNoUuid.json")
        )
    }
}
