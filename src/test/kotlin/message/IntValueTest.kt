package cz.smarteon.loxone.message

import cz.smarteon.loxone.readValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class IntValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue<IntValue>("\"48\"").value).isEqualTo(48)
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(IntValue::class.java).usingGetClass().verify()
    }
}
