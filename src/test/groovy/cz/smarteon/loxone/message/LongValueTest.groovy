package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class LongValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        LongValue longValue = readValue('"220283340"', LongValue)

        then:
        longValue.value == 220283340l
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(LongValue).usingGetClass().verify()
    }
}
