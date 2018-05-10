package cz.smarteon.loxone

import cz.smarteon.loxone.message.SerializationSupport
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Shared
import spock.lang.Specification


class LoxoneUuidTest extends Specification implements SerializationSupport {

    @Shared LoxoneUuid testUuid = new LoxoneUuid(260481790, 888, 15880, [0xff, 0xff, 0xb2, 0xd4, 0xef, 0xc8, 0xb5, 0xb6] as byte[])
    @Shared String testUuidString = '0f86a2fe-0378-3e08-ffffb2d4efc8b5b6'

    def "should serialize"() {
        expect:
        writeValue(testUuid) == "\"$testUuidString\""
    }

    def "should deserialize"() {
        expect:
        readValue("\"$testUuidString\"", LoxoneUuid) == testUuid
    }

    def "should have toString"() {
        expect:
        testUuid.toString() == testUuidString
    }

    def "should create from string"() {
        expect:
        new LoxoneUuid(testUuidString) == testUuid
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(LoxoneUuid).verify()
    }
}
