package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class TokenTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        Token token = readResource('message/token.json', Token)

        then:
        token.token == '8E2AA590E996B321C0E17C3FA9F7A3C17BD376CC'
        token.key == [68, 68, 50] as byte[]
        token.validUntil == 342151839
        token.rights == 1666
        token.unsecurePassword == false
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Token).usingGetClass().verify()
    }
}
