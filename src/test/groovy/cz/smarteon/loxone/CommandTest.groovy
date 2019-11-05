package cz.smarteon.loxone

import spock.lang.Specification

import static cz.smarteon.loxone.Command.voidWsCommand

class CommandTest extends Specification {

    def "test voidWsCommand"() {
        when:
        def cmd = voidWsCommand('CMD/%s%s', 'aa', 'bb')

        then:
        cmd.command == 'CMD/aabb'
    }
}
