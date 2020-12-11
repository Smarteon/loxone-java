package cz.smarteon.loxone

import cz.smarteon.loxone.app.MiniserverType
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static cz.smarteon.loxone.Command.voidWsCommand

class CommandTest extends Specification {

    def "test voidWsCommand"() {
        when:
        def cmd = voidWsCommand(MiniserverType.KNOWN, 'CMD/%s%s', 'aa', 'bb')

        then:
        cmd.command == 'CMD/aabb'
        cmd.shouldContain == 'CMD/aabb'
    }

    def "test json command"() {
        when:
        def cmd = new Command('jdev/test', Command.Type.JSON, String.class, false, true, MiniserverType.FIRST_GEN)

        then:
        cmd.command == 'jdev/test'
        cmd.shouldContain == 'dev/test'
        cmd.supportedMiniservers == MiniserverType.FIRST_GEN
        !cmd.supportsMiniserver(MiniserverType.REGULAR_V2)
        cmd.is('dev/test/something')
        !cmd.is('something')
    }

    def "should ensure response"() {
        when:
        def cmd = new Command('jdev/test', Command.Type.JSON, String.class, false, true, MiniserverType.KNOWN)

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

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Command).usingGetClass().verify()
    }
}
