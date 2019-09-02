package cz.smarteon.loxone.config

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class MiniserverUserTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        MiniserverUser user = readResource('config/miniserverUser.json', MiniserverUser)

        then:
        user.name == 'showroom'
        user.uuid == new LoxoneUuid('0f86a25d-026f-1c1e-ffffd4c75dbaf53c')
        user.admin
        user.canChangePassword()
        user.rights == 2047
    }
}
