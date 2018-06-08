package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class MessageHeaderTest extends Specification {

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(MessageHeader).verify()
    }
}
