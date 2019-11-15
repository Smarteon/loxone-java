package cz.smarteon.loxone.message

import spock.lang.Specification

class ControlCommandTest extends Specification {

    def "test basic control command"() {
        when:
        def cmd = new ControlCommand('uuid', 'op', IntValue.class)

        then:
        cmd.command == 'jdev/sps/io/uuid/op'
        cmd.controlCommand == 'uuid/op'
    }
}
