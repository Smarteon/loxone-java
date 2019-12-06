package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class HashingTest extends Specification implements SerializationSupport {

    @Unroll
    def "should deserialize #version"() {
        when:
        Hashing hashing = readResource("message/hashing_${version}.json", Hashing)

        then:
        hashing.key == [0x41, 0x43] as byte[]
        hashing.salt == '3130'
        hashing.hashAlg == hashAlg;

        where:
        version | hashAlg
        '10_2'  | null
        '10_3'  | 'SHA1'
    }

    @Unroll
    def "should serialize #version"() {
        expect:
        that new Hashing([0x41, 0x43] as byte[], '3130', hashAlg),
                jsonEquals(resource("message/hashing_${version}.json"))

        where:
        version | hashAlg
        '10_2'  | null
        '10_3'  | 'SHA1'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Hashing).usingGetClass().verify()
    }
}
