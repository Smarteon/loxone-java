package cz.smarteon.loxone.message

import spock.lang.Specification


class ApiInfoTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        ApiInfo apiInfo = readResource('/message/apiInfo.json', ApiInfo)

        then:
        apiInfo.mac == '50:4F:94:10:B8:4A'
        apiInfo.version == '9.1.10.30'
    }
}
