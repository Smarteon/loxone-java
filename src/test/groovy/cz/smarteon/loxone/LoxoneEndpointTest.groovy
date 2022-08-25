package cz.smarteon.loxone

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

class LoxoneEndpointTest2 extends Specification {

    @Unroll
    def "test #testCase"() {
        expect:
        endpoint.webSocketUri().toString() == expectedWs
        endpoint.httpUrl('c').toString() == expectedHttp

        where:
        testCase   | endpoint                                  || expectedWs                     | expectedHttp
        'defaults' | new LoxoneEndpoint('testAddr')            || 'ws://testAddr:80/ws/rfc6455'  | 'http://testAddr:80/c'
        'port'     | new LoxoneEndpoint('testAddr', 34)        || 'ws://testAddr:34/ws/rfc6455'  | 'http://testAddr:34/c'
        'no-ssl'   | new LoxoneEndpoint('testAddr', 34, false) || 'ws://testAddr:34/ws/rfc6455'  | 'http://testAddr:34/c'
        'ssl'      | new LoxoneEndpoint('testAddr', 34, true)  || 'wss://testAddr:34/ws/rfc6455' | 'https://testAddr:34/c'
    }

    def "test toString()"() {
        expect:
        endpoint.toString() == toString

        where:
        endpoint                              | toString
        new LoxoneEndpoint('addr', 80, false) | 'addr:80 (unsecured)'
        new LoxoneEndpoint('addr', 80, true)  | 'addr:80 (secured)'
    }
    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(LoxoneEndpoint).withNonnullFields("host", "path").verify()
    }
}
