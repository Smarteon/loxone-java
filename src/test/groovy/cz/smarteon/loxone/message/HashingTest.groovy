package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static cz.smarteon.loxone.message.TestHelper.readResource

class HashingTest extends Specification {

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
