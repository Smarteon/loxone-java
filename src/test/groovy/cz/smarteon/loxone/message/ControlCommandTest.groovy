package cz.smarteon.loxone.message

import cz.smarteon.loxone.app.MiniserverType
import spock.lang.Specification

class ControlCommandTest extends Specification {

    def "test basic control command"() {
        when:
        def cmd = new ControlCommand('uuid', 'op', IntValue.class, MiniserverType.KNOWN)

        then:
        cmd.command == 'jdev/sps/io/uuid/op'
        cmd.controlCommand == 'uuid/op'
        cmd.supportedMiniservers == MiniserverType.KNOWN
    }
}
