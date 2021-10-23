package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class MiniserverUserTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        MiniserverUser user = readResource('app/miniserverUser.json', MiniserverUser)

        then:
        user.name == 'showroom'
        user.uuid == new LoxoneUuid('0f86a25d-026f-1c1e-ffffd4c75dbaf53c')
        user.admin
        user.canChangePassword()
        Arrays.equals(user.rights, [0xFF, 0xFF, 0xFF, 0xFF] as byte[])
    }
}
