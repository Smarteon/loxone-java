package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class OneWireDetailsTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        OneWireDetails oneWireDetails = readResource('message/oneWireDetails.json', OneWireDetails)

        then:
        oneWireDetails.asMap().size() == 2
        with(oneWireDetails.asMap()['28.BB.CE.AD.07.00.00.2F']) {
            serial == '28.BB.CE.AD.07.00.00.2F'
            packetRequests == 2709
            crcErrors == 0
            _85DegreeErrors == 0
        }
    }

    def "should deserialize error"() {
        when:
        OneWireDetails oneWireDetails = readValue("\"$errorValue\"", OneWireDetails)

        then:
        oneWireDetails.isInvalid()
        oneWireDetails.invalid == errorValue

        where:
        errorValue << [ '', 'timeout' ]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(OneWireDetails).usingGetClass().verify()
    }
}
