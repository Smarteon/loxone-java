package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryEveryYearTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/everyYear.json"))
            .isA<CalEntryEveryYear>().and {
                get { uuid }.isLoxoneUuid("1b6f47bd-0390-9b1e-ffff504f94a03d3e")
                get { name }.isEqualTo("Every year test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(0)
                get { startMonth }.isEqualTo(4)
                get { startDay }.isEqualTo(1)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Every year test/1/0/4/1")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntryEveryYear("Every year test", 1, 4, 1),
            readResource<CalEntryBase>("calendar/everyYearNoUuid.json")
        )
    }
}
