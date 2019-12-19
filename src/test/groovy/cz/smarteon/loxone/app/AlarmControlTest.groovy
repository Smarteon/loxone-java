package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class AlarmControlTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        def control = readResource("app/alarmControl.json", AlarmControl)

        then:
        control.uuid == new LoxoneUuid('0f86a2fe-0378-3e15-ffff373f9870b52a')
        control.name == 'Alarm'
        control.secured
        control.stateArmed() == new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
        control.stateArmedDelay() == new LoxoneUuid('0f86a2fe-0378-3e11-ffffb2d4efc8b5b6')
        control.stateArmedDelayTotal() == new LoxoneUuid('0f86a2fe-0378-3dfb-ffffb2d4efc8b5b6')
        control.stateLevel() == new LoxoneUuid('10a73e3b-01c5-1902-ffff373f9870b52a')
        control.stateNextLevel() == new LoxoneUuid('10a73e3b-01c5-18ff-ffff373f9870b52a')
        control.stateNextLevelDelay() == new LoxoneUuid('10a73e3b-01c5-1900-ffff373f9870b52a')
        control.stateNextLevelDelayTotal() == new LoxoneUuid('10a73e3b-01c5-1901-ffff373f9870b52a')
        control.stateDisabledMove() == new LoxoneUuid('10a73e3b-01c5-1904-ffff373f9870b52a')
        control.stateSensors() == new LoxoneUuid('0f86a2fe-0378-3e15-ffff373f9870b52a')
        control.stateStartTime() == new LoxoneUuid('10a73e3b-01c5-1903-ffff373f9870b52a')
    }
}
