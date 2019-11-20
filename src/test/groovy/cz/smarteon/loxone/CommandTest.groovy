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

    def "should ensure response"() {
        when:
        def cmd = new Command('jdev/test', Command.Type.JSON, String.class, false, true)

        then:
        cmd.ensureResponse('aa') == 'aa'

        when:
        cmd.ensureResponse(null)

        then:
        thrown(LoxoneException)

        when:
        cmd.ensureResponse(3)

        then:
        thrown(LoxoneException)
    }
}
