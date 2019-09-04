package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class HashingTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        Hashing hashing = readResource('message/hashing.json', Hashing)

        then:
        hashing.key == [0x41, 0x43] as byte[]
        hashing.salt == '3130'
    }

    def "should serialize"() {
        expect:
        that new Hashing([0x41, 0x43] as byte[], '3130'), jsonEquals(resource('message/hashing.json'))
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Hashing).usingGetClass().verify()
    }
}
