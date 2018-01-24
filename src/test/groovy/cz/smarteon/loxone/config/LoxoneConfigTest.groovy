package cz.smarteon.loxone.config

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class LoxoneConfigTest extends Specification implements SerializationSupport {

    private static final Date LAST_MODIFIED = new Date(117, 10, 22, 19, 41, 1)
    private static final LoxoneUuid UUID = new LoxoneUuid('0f869a64-0200-0a9b-ffffd4c75dbaf53c')

    def "should deserialize"() {
        when:
        LoxoneConfig config = readResource('/config/LoxAPP3.json', LoxoneConfig)

        then:
        config.lastModified == LAST_MODIFIED
        config.controls.size() == 6
        config.controls.values().first() instanceof AlarmControl
    }

    def "should getControlOfType"() {
        given:
        LoxoneConfig config = new LoxoneConfig(LAST_MODIFIED, [(UUID) : alarmControl])

        expect:
        config.getControl(AlarmControl) == alarmControl

        where:
        alarmControl << [new AlarmControl(), Stub(AlarmControl)]
    }
}
