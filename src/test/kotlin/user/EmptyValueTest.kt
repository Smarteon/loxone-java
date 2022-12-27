package cz.smarteon.loxone.user

import cz.smarteon.loxone.readValue
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty

class EmptyValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue<EmptyValue>("\"\"").empty).isEmpty()
    }
}
