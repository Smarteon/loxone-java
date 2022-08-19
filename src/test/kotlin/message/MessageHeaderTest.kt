package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class MessageHeaderTest {

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(MessageHeader::class.java).usingGetClass().verify()
    }
}
