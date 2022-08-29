package cz.smarteon.loxone.message

import cz.smarteon.loxone.readValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class HeapTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue<Heap>("\"27783/50708kB\"")) {
            get { used }.isEqualTo(27783)
            get { allowed }.isEqualTo(50708)
        }
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(Heap::class.java).usingGetClass().verify()
    }
}
