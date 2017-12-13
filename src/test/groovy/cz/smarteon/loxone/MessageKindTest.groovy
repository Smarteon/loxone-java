package cz.smarteon.loxone

import cz.smarteon.loxone.message.MessageKind
import spock.lang.Specification
import spock.lang.Unroll


class MessageKindTest extends Specification {

    @Unroll
    def "valueOf(#byteValHex) should be #kind"() {
        expect:
        MessageKind.valueOf(byteVal) == kind

        where:
        kind << MessageKind.values()
        byteVal = (byte) kind.ordinal()
        byteValHex = '0x' + ([byteVal] as byte[]).encodeHex()
    }
}
