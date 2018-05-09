package cz.smarteon.loxone.message

import spock.lang.Specification

class DateValueTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        given:
        def date = getDate()

        when:
        DateValue dateValue = readValue("\"${formatDate(date)}\"", DateValue)

        then:
        dateValue.date == date
    }
}
