package cz.smarteon.loxone.config

import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification
import spock.lang.Unroll

import static cz.smarteon.loxone.config.TemperatureUnit.CELSIUS
import static cz.smarteon.loxone.config.TemperatureUnit.FAHRENHEIT

class TemperatureUnitTest extends Specification implements SerializationSupport {

    @Unroll
    def "should deserialize #unit from #value"() {
        expect:
        readValue(value, TemperatureUnit) == unit

        where:
        value || unit
        "0"   || CELSIUS
        "1"   || FAHRENHEIT
    }
}
