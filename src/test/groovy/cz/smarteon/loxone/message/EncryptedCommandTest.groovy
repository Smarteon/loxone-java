package cz.smarteon.loxone.message

import spock.lang.Specification

class EncryptedCommandTest extends Specification {

    def "should encrypt"() {
        when:
        def cmd = new EncryptedCommand('myCmd%', Void, { c -> 'ENCRYPTED->' + c })

        then:
        cmd.command == 'jdev/sys/enc/ENCRYPTED-%3EmyCmd%25'
    }
}
