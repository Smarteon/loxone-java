package cz.smarteon.loxone

import cz.smarteon.loxone.app.MiniserverType
import spock.lang.Specification

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand

class SecuredCommandTest extends Specification {

    def "test basic secured command"() {
        when:
        def cmd = new SecuredCommand(genericControlCommand('uuid', 'op', MiniserverType.FIRST_GEN), 'visuhash')

        then:
        cmd.command == 'jdev/sps/ios/visuhash/uuid/op'
        cmd.supportedMiniservers == MiniserverType.FIRST_GEN
    }
}
