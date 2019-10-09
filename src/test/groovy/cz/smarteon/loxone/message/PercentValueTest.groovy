package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class PercentValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        PercentValue percentValue = readValue(value, PercentValue)

        then:
        percentValue.value == expected

        where:
        value   || expected
        '"99%"' || 99
        '""'    || -1
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PercentValue).usingGetClass().verify()
    }
}
