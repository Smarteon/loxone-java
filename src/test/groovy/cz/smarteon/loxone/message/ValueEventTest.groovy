package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import spock.lang.Specification

class ValueEventTest extends Specification {

    def "should have properties"() {
        when:
        def uuid = new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
        def event = new ValueEvent(uuid, 3.7)

        then:
        event.uuid == uuid
        event.value == 3.7 as Double
    }
}
