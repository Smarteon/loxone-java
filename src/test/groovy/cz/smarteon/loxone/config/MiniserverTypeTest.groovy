package cz.smarteon.loxone.config

import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification
import spock.lang.Unroll

import static cz.smarteon.loxone.config.MiniserverType.GO
import static cz.smarteon.loxone.config.MiniserverType.REGULAR

class MiniserverTypeTest extends Specification implements SerializationSupport {

    @Unroll
    def "should deserialize #type from #value"() {
        expect:
        readValue(value, MiniserverType) == type

        where:
        value || type
        "0"   || REGULAR
        "1"   || GO
    }
}
