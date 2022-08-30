package cz.smarteon.loxone

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LoxoneUuidTest {

    @Test
    fun `should serialize`() {
        expectThat(writeValue(testUuid)).isEqualTo("\"$testUuidString\"")
    }

    @Test
    fun `should deserialize`() {
        expectThat(readValue<LoxoneUuid>("\"$testUuidString\"",)).isEqualTo(testUuid)
    }

    @Test
    fun `should have toString`() {
        expectThat(testUuid.toString()).isEqualTo(testUuidString)
    }

    @Test
    fun `should create from string`() {
        expectThat(LoxoneUuid(testUuidString)).isEqualTo(testUuid)
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(LoxoneUuid::class.java).verify()
    }

    companion object {
        private val testUuid = LoxoneUuid(
            260481790, 888, 15880, byteArrayOf(
                0xff.toByte(),
                0xff.toByte(), 0xb2.toByte(), 0xd4.toByte(), 0xef.toByte(), 0xc8.toByte(), 0xb5.toByte(), 0xb6.toByte()
            )
        )
        private const val testUuidString = "0f86a2fe-0378-3e08-ffffb2d4efc8b5b6"
    }
}
