package cz.smarteon.loxone.app

import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification
import spock.lang.Unroll

import static cz.smarteon.loxone.app.MiniserverType.GO
import static cz.smarteon.loxone.app.MiniserverType.REGULAR
import static cz.smarteon.loxone.app.MiniserverType.REGULAR_V2
import static cz.smarteon.loxone.app.MiniserverType.UNKNOWN

class MiniserverTypeTest extends Specification implements SerializationSupport {

    @Unroll
    def "should deserialize #type from #value"() {
        expect:
        readValue(value, MiniserverType) == type

        where:
        value || type
        "0"   || REGULAR
        "1"   || GO
        "2"   || REGULAR_V2
        "3"   || UNKNOWN
        "5"   || UNKNOWN
    }
}
