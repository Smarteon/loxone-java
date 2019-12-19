package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class PresenceControlTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        def control = readResource("app/presenceControl.json", PresenceControl)

        then:
        control.uuid == new LoxoneUuid('1471d837-00ec-90e3-ffffc0f606ef595c')
        control.name == 'PresTest'
    }
}
