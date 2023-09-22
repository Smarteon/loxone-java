package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryEasterOffsetTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryBase>("calendar/easterOffset.json"))
            .isA<CalEntryEasterOffset>().and {
                get { uuid }.isLoxoneUuid("1b6f47dd-0243-9b2c-ffff504f94a03d3e")
                get { name }.isEqualTo("Eastern offset test")
                get { operatingMode }.isEqualTo(1)
                get { calMode }.isEqualTo(1)
                get { easterOffset }.isEqualTo(11)
                get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Eastern offset test/1/1/11")
                get { updateEntryCommand(this.uuid).command }.isEqualTo("jdev/sps/calendarupdateentry/1b6f47dd-0243-9b2c-ffff504f94a03d3e/Eastern offset test/1/1/11")
            }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            CalEntryEasterOffset("Eastern offset test", 1, 11),
            readResource<CalEntryBase>("calendar/easterOffsetNoUuid.json")
        )
    }
}
