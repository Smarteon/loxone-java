package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class IntValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        IntValue intValue = readValue('"48"', IntValue)

        then:
        intValue.value == 48
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(IntValue).usingGetClass().verify()
    }
}
