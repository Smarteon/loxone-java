package cz.smarteon.loxone.message

import spock.lang.Specification

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class LoxoneMessageTest extends Specification implements SerializationSupport {


    private static final Hashing HASHING = new Hashing(
            '41434633443134324337383441373035453333424344364133373431333430413642333442334244'.decodeHex(),
            '31306137336533622D303163352D313732662D66666666616362383139643462636139'
    )

    def "should deserialize get key"() {
        when:
        LoxoneMessage message = readResource('message/getKeyMessage.json', LoxoneMessage)

        then:
        message
        message.control == 'jdev/sys/getkey2/showroom'
        message.code == 200
        message.value == HASHING
    }

    def "should serialize"() {
        expect:
        that new LoxoneMessage( 'jdev/sys/getkey2/showroom', 200, HASHING), jsonEquals(resource('message/getKeyMessage.json'))
    }

    def "should deserialize alarm all"() {
        expect:
        readResource('message/alarmAll.json', LoxoneMessage)
    }
}
