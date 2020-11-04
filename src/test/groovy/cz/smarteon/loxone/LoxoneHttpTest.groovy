package cz.smarteon.loxone


import cz.smarteon.loxone.message.IntValue
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneMessageCommand
import net.jadler.Jadler
import net.jadler.stubbing.server.jdk.JdkStubHttpServer
import spock.lang.Specification
import spock.lang.Subject

import static cz.smarteon.loxone.Command.Type.JSON
import static net.jadler.Jadler.closeJadler
import static net.jadler.Jadler.initJadlerUsing
import static net.jadler.Jadler.onRequest

class LoxoneHttpTest extends Specification {

    @Subject LoxoneHttp loxoneHttp

    def setup() {
        initJadlerUsing(new JdkStubHttpServer())
        loxoneHttp = new LoxoneHttp(new LoxoneEndpoint('localhost', Jadler.port()))
    }

    void cleanup() {
        closeJadler()
    }

    def "should get cfg"() {
        given:
        onRequest()
            .havingMethodEqualTo('GET')
            .havingPathEqualTo('/jdev/sys/numtasks')
            .respond()
            .withBody('{"LL":{"control": "dev/sys/numtasks", "value": "1", "code": 200}}')

        when:
        LoxoneMessage<IntValue> numtasks = loxoneHttp.get(LoxoneMessageCommand.DEV_SYS_NUMTASKS)

        then:
        numtasks.value.value == 1
    }

    def "should redirect"() {
        given:
        def finalLocation = "http://localhost:${Jadler.port()}/test2"
        onRequest()
            .havingMethodEqualTo('GET')
            .havingPathEqualTo('/test')
            .respond()
            .withStatus(302)
            .withHeader('Location', finalLocation)
        onRequest()
            .havingMethodEqualTo('GET')
            .havingPathEqualTo('/test2')
            .respond()
            .withStatus(200)
            .withBody('"testString"')

        when:
        def result = loxoneHttp.get(new Command<>('/test', JSON, String.class, true, false))

        then:
        result == 'testString'
        loxoneHttp.lastUrl == new URL(finalLocation)
    }
}
