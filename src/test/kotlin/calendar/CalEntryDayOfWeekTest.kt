package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryDayOfWeekTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/dayOfWeek.json"))
            .isA<CalEntryDayOfWeek>().and {
                get { uuid }.isLoxoneUuid("1b6f49d6-030f-9bc2-ffff504f94a03d3e")
                get { name }.isEqualTo("Day of week test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(5)
                get { startMonth }.isEqualTo(7)
                get { weekDayInMonth}.isEqualTo(1)
                get { weekDay }.isEqualTo(2)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Day of week test/1/5/1/2/7")
                get { updateEntryCommand(this.uuid).command }.isEqualTo("jdev/sps/calendarupdateentry/1b6f49d6-030f-9bc2-ffff504f94a03d3e/Day of week test/1/5/1/2/7")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntryDayOfWeek("Day of week test", 1, 2, 7, CalEntryDayOfWeek.WeekDayInMonth.FIRST_WEEKDAY),
            readResource<CalEntryBase>("calendar/dayOfWeekNoUuid.json")
        )
    }
}
