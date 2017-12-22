package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class HashingTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        Hashing hashing = readResource('/message/hashing.json', Hashing)

        then:
        hashing.key == [0x41, 0x43] as byte[]
        hashing.salt == '3130'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Hashing).usingGetClass().verify()
    }
}
