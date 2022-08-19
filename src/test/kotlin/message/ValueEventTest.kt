package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ValueEventTest {

    @Test
    fun `should have properties`() {
        val expectedUuid = LoxoneUuid("0f86a2fe-0378-3e08-ffffb2d4efc8b5b6")
        val expectedValue = 3.7
        val event = ValueEvent(expectedUuid, expectedValue)

        expectThat(event) {
            get { uuid }.isEqualTo(expectedUuid)
            get { value }.isEqualTo(expectedValue)
        }
    }
}
