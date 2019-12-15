package cz.smarteon.loxone.message

import spock.lang.Specification
import spock.lang.Unroll

import static cz.smarteon.loxone.message.TokenPermissionType.APP
import static cz.smarteon.loxone.message.TokenPermissionType.WEB

class TokenPermissionTypeTest extends Specification {

    @Unroll
    def "#type should have id=#id"() {
        expect:
        type.getId() == id

        where:
        type || id
        WEB  || 2
        APP  || 4
    }
}
