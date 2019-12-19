package cz.smarteon.loxone.message

import cz.smarteon.loxone.LoxoneUuid
import spock.lang.Specification

class TextEventTest extends Specification {

    def "should have properties"() {
        when:
        def uuid = new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
        def icon = new LoxoneUuid('00000000-0000-0000-0000000000000000')
        def event = new TextEvent(uuid, icon, 'someText')

        then:
        event.uuid == uuid
        event.iconUuid == icon
        event.text == 'someText'
    }
}
