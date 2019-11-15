package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class LoxoneConfigTest extends Specification implements SerializationSupport {

    private static final Date LAST_MODIFIED = parseDate('2017-11-22 18:41:01')
    private static final LoxoneUuid UUID = new LoxoneUuid('0f869a64-0200-0a9b-ffffd4c75dbaf53c')

    def "should deserialize"() {
        when:
        LoxoneApp config = readResource('config/LoxAPP3.json', LoxoneApp)

        then:
        config.lastModified == LAST_MODIFIED
        config.miniserverInfo?.name == 'ShowRoom'
        config.controls.size() == 6
        config.controls.values().first() instanceof AlarmControl
    }

    def "should getControl by type"() {
        given:
        LoxoneApp config = new LoxoneApp(LAST_MODIFIED, null, [(UUID): alarmControl])

        expect:
        config.getControl(AlarmControl) == alarmControl

        where:
        alarmControl << [new AlarmControl(), Stub(AlarmControl)]
    }

    def "should getControl by name and type"() {
        given:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        LoxoneApp config = new LoxoneApp(LAST_MODIFIED, null, [(UUID): control])

        expect:
        config.getControl('SomeControl', SwitchControl) == control
    }
}
