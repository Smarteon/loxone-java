package cz.smarteon.loxone.message

import cz.smarteon.loxone.formatDate
import cz.smarteon.loxone.getDate
import cz.smarteon.loxone.readValue
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DateValueTest {

    @Test
    fun `should deserialize`() {
        val expectedDate = getDate()

        expectThat(readValue("\"${formatDate(expectedDate)}\"", DateValue::class)) {
            get { date }.isEqualTo(expectedDate)
        }
    }
}
