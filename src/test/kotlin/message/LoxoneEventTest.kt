package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class LoxoneEventTest {

    @Test
    fun `should have uuid`() {
        val expectedUuid = LoxoneUuid("0f86a2fe-0378-3e08-ffffb2d4efc8b5b6")
        val event = object : LoxoneEvent(expectedUuid) {}

        expectThat(event) {
            get { uuid }.isEqualTo(expectedUuid)
        }
    }

    @Test
    fun `cant have null uuid`() {
        expectThrows<Exception> { object : LoxoneEvent(LoxoneUuid(null)) {} }
    }
}
