package cz.smarteon.loxone.message

import cz.smarteon.loxone.readResource
import cz.smarteon.loxone.readValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

class OneWireDetailsTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource("message/oneWireDetails.json", OneWireDetails::class)) {
            get { asMap().size }.isEqualTo(2)
            get { asMap()["28.BB.CE.AD.07.00.00.2F"] }.isNotNull().and {
                get { serial }.isEqualTo("28.BB.CE.AD.07.00.00.2F")
                get { packetRequests }.isEqualTo(2709)
                get { crcErrors }.isEqualTo(0)
                get { _85DegreeErrors }.isEqualTo(0)
            }
        }
    }

    @Test
    fun `should deserialize error`() {
        expectThat(readValue("\"timeout\"", OneWireDetails::class)) {
            get { isInvalid }.isTrue()
            get { invalid }.isEqualTo("timeout")
        }
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(OneWireDetails::class.java).usingGetClass().verify()
    }
}
