package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CalEntryListValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<CalEntryListValue>("calendar/testEntries.json")) {
           get { this[3] }.isA<CalEntryEasterOffset>().and {
               get { uuid }.isLoxoneUuid("1b6f47dd-0243-9b2c-ffff504f94a03d3e")
               get { name }.isEqualTo("Eastern offset test")
               get { operatingMode }.isEqualTo(1)
               get { calMode }.isEqualTo(1)
               get { easterOffset }.isEqualTo(11)
               get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Eastern offset test/1/1/11")
           }
           get { this[4] }.isA<CalEntrySingleDay>().and {
               get { uuid }.isLoxoneUuid("1b6f47f0-03b0-9b3d-ffff504f94a03d3e")
               get { name }.isEqualTo("Single day test")
               get { operatingMode }.isEqualTo(1)
               get { calMode }.isEqualTo(2)
               get { startYear }.isEqualTo(2024)
               get { startMonth }.isEqualTo(8)
               get { startDay }.isEqualTo(3)
               get { createEntryCommand().command }.isEqualTo("jdev/sps/calendarcreateentry/Single day test/1/2/2024/8/3")
           }
        }
    }
}
