package cz.smarteon.loxone.message

import spock.lang.Specification

class EncryptedCommandTest extends Specification {

    def "should encrypt"() {
        when:
        def cmd = new EncryptedCommand('myCmd%', Void, { c -> 'ENCRYPTED->' + c })

        then:
        cmd.command == 'jdev/sys/enc/ENCRYPTED-%3EmyCmd%25'
    }

    def "should create gettoken"() {
        when:
        def cmd = EncryptedCommand.getToken('testHash', 'testUser', TokenPermissionType.APP, 'testUuid', 'testClient',  { c -> 'ENCRYPTED->' + c })

        then:
        cmd.command == 'jdev/sys/enc/ENCRYPTED-%3Ejdev%2Fsys%2Fgettoken%2FtestHash%2FtestUser%2F4%2FtestUuid%2FtestClient'
    }
}
