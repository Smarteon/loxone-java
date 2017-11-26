package cz.smarteon.loxone.message

import spock.lang.Specification

import static cz.smarteon.loxone.message.TestHelper.MAPPER


class JsonValueTest extends Specification {

    def "should deserialize"() {
        expect:
        MAPPER.readValue(json, JsonValue)

        where:
        json << ['""', '123', '{}', '[null, -6]', '{"a":null,"b":34.5}']
    }
}
