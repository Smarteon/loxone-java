package cz.smarteon.loxone.config

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification


class AlarmControlTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        AlarmControl control = readResource('/config/alarmControl.json', Control)

        then:
        control.name == 'Alarm'
        control.states.size() == 10
        control.armed == new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
    }
}
