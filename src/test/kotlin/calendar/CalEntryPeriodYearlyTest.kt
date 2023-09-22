package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryPeriodYearlyTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/periodYearly.json"))
            .isA<CalEntryPeriodYearly>().and {
                get { uuid }.isLoxoneUuid("1b6f49be-025c-9ba0-ffff504f94a03d3e")
                get { name }.isEqualTo("Period every year test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(4)
                get { startMonth }.isEqualTo(7)
                get { startDay }.isEqualTo(1)
                get { endMonth }.isEqualTo(7)
                get { endDay }.isEqualTo(6)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Period every year test/1/4/7/1/7/6")
                get { updateEntryCommand(this.uuid).command }.isEqualTo("jdev/sps/calendarupdateentry/1b6f49be-025c-9ba0-ffff504f94a03d3e/Period every year test/1/4/7/1/7/6")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntryPeriodYearly("Period every year test", 1, 7, 1, 7, 6),
            readResource<CalEntryBase>("calendar/periodYearlyNoUuid.json")
        )
    }
}
