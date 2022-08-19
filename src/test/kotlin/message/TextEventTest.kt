package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TextEventTest {

    @Test
    fun `should have properties`() {
        val expectedUuid = LoxoneUuid("0f86a2fe-0378-3e08-ffffb2d4efc8b5b6")
        val icon = LoxoneUuid("00000000-0000-0000-0000000000000000")

        expectThat(TextEvent(expectedUuid, icon, "someText")) {
            get { uuid }.isEqualTo(expectedUuid)
            get { iconUuid }.isEqualTo(icon)
            get { text }.isEqualTo("someText")
        }
    }
}
