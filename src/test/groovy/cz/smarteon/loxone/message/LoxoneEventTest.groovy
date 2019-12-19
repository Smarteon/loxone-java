package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import spock.lang.Specification

class LoxoneEventTest extends Specification {

    def "should have uuid"() {
        when:
        def uuid = new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
        def event = new LoxoneEvent(uuid) {}

        then:
        event.uuid == uuid
    }

    def "can't have null uuid"() {
        when:
        new LoxoneEvent(null){}

        then:
        thrown(Exception)
    }
}
