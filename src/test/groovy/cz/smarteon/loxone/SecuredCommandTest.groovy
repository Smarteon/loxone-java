package cz.smarteon.loxone


import spock.lang.Specification

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand

class SecuredCommandTest extends Specification {

    def "test basic secured command"() {
        when:
        def cmd = new SecuredCommand(genericControlCommand('uuid', 'op'), 'visuhash')

        then:
        cmd.command == 'jdev/sps/ios/visuhash/uuid/op'
    }
}
