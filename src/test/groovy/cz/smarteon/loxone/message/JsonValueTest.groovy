package cz.smarteon.loxone.message

import com.fasterxml.jackson.databind.node.TextNode
import cz.smarteon.loxone.LoxoneException
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

    def "should convert to primitives"() {
        expect:
        readValue(json, JsonValue).as(type)

        where:
        json          | type
        '"20"'        | IntValue
        '"220283340"' | LongValue
    }

    def "should not convert incompatible"() {
        when:
        new JsonValue(TextNode.valueOf('notInt')).as(IntValue)

        then:
        thrown(LoxoneException)
    }
}
