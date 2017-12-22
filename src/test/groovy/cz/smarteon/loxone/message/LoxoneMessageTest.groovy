package cz.smarteon.loxone.message

import cz.smarteon.loxone.Codec
import spock.lang.Specification

class LoxoneMessageTest extends Specification implements SerializationSupport {

    def "should deserialize get key"() {
        when:
        LoxoneMessage message = readResource('/message/getKeyMessage.json', LoxoneMessage)

        then:
        message
        message.control == 'jdev/sys/getkey2/showroom'
        message.code == 200
        message.value == new Hashing(
                Codec.hexToBytes('41434633443134324337383441373035453333424344364133373431333430413642333442334244'),
                '31306137336533622D303163352D313732662D66666666616362383139643462636139'
        )
    }

    def "should deserialize alarm all"() {
        expect:
        readResource('/message/alarmAll.json', LoxoneMessage)
    }
}
