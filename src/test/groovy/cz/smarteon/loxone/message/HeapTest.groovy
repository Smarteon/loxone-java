package cz.smarteon.loxone.message

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class HeapTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        Heap heap = readValue('"27783/50708kB"', Heap)

        then:
        heap.used == 27783
        heap.allowed == 50708
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Heap).usingGetClass().verify()
    }
}
