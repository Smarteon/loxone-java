package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class PercentValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        PercentValue percentValue = readValue('"99%"', PercentValue)

        then:
        percentValue.value == 99
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PercentValue).usingGetClass().verify()
    }
}
