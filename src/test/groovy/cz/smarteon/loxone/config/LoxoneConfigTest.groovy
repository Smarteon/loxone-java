package cz.smarteon.loxone.config

import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class LoxoneConfigTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        LoxoneConfig config = readResource('/config/LoxAPP3.json', LoxoneConfig)

        then:
        config.lastModified == new Date(117, 10, 22, 19, 41, 1)
        config.controls.size() == 6
        config.controls.values().first() instanceof AlarmControl
    }
}
