package cz.smarteon.loxone.message

import com.fasterxml.jackson.databind.node.TextNode
import spock.lang.Specification

class JsonValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        expect:
        readValue(json, JsonValue)

        where:
        json << ['""', '123', '{}', '[null, -6]', '{"a":null,"b":34.5}']
    }

    def "should serialize"() {
        expect:
        writeValue(new JsonValue(new TextNode('haha'))) == '"haha"'
    }
}
