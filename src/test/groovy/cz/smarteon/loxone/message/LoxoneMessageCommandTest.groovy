package cz.smarteon.loxone.message

import cz.smarteon.loxone.Command
import cz.smarteon.loxone.LoxoneException
import cz.smarteon.loxone.app.MiniserverType
import spock.lang.Specification

class LoxoneMessageCommandTest extends Specification {

    def "should ensure value"() {
        when:
        def cmd = new LoxoneMessageCommand('jdev/test', Command.Type.JSON, IntValue.class, false, true, MiniserverType.KNOWN)

        then:
        cmd.ensureValue(new IntValue(5)).value == 5

        when:
        cmd.ensureValue(null)

        then:
        thrown(LoxoneException)

        when:
        cmd.ensureValue(new LongValue("0"))

        then:
        thrown(LoxoneException)
    }
}
