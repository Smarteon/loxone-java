package cz.smarteon.loxone.message

import spock.lang.Specification


class DateValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        DateValue dateValue = MAPPER.readValue('"2017-11-22 18:41:01"', DateValue)

        then:
        dateValue.date == new Date(117, 10, 22, 19, 41, 1)
    }
}
