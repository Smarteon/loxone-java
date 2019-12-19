package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class TechnicalAlarmControlTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        def control = readResource("app/technicalAlarmControl.json", TechnicalAlarmControl)

        then:
        control.uuid == new LoxoneUuid('10a75401-013a-1272-fffff8bbb0459c02')
        control.name == 'Technical alarm'
        !control.secured
        control.stateLevel() == new LoxoneUuid('1351c8fb-0264-7ef4-fffff8bbb0459c02')
        control.stateNextLevel() == new LoxoneUuid('1351c8fb-0264-7ef5-fffff8bbb0459c02')
        control.stateNextLevelDelay() == new LoxoneUuid('1351c8fb-0264-7ef6-fffff8bbb0459c02')
        control.stateNextLevelDelayTotal() == new LoxoneUuid('1351c8fb-0264-7ef7-fffff8bbb0459c02')
        control.stateSensors() == new LoxoneUuid('10a75401-013a-1272-fffff8bbb0459c02')
        control.stateStartTime() == new LoxoneUuid('1351c8fb-0264-7efa-fffff8bbb0459c02')
        control.stateAlarmCause() == new LoxoneUuid('1351c8fb-0264-7ef9-fffff8bbb0459c02')
        control.stateAcousticAlarm() == new LoxoneUuid('10a75401-013a-126e-ffff9084f3b66cb8')
        control.stateTestAlarm() == new LoxoneUuid('10a75401-013a-1270-ffff9084f3b66cb8')
        control.stateTimeServiceMode() == new LoxoneUuid('1351c8fb-0264-7ef8-fffff8bbb0459c02')
        control.stateAreAlarmSignalsOff() == new LoxoneUuid('1351c8fb-0264-7efb-fffff8bbb0459c02')
    }
}
