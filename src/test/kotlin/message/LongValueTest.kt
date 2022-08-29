package cz.smarteon.loxone.message

import cz.smarteon.loxone.readValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LongValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue<LongValue>("\"220283340\"").value).isEqualTo(220283340)
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(LongValue::class.java).usingGetClass().verify()
    }
}
