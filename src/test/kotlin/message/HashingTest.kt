package cz.smarteon.loxone.message

import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class HashingTest {

    enum class TestVersions(
        val version: String,
        val hashAlg: String?
    ) {
        Version10_2("10_2", null),
        Version10_3("10_3", "SHA1");
    }

    @ParameterizedTest
    @EnumSource(TestVersions::class)
    fun `should deserialize`(testParameters: TestVersions) {
        expectThat(readResource<Hashing>("message/hashing_${testParameters.version}.json")) {
            get { key }.isEqualTo(byteArrayOf(0x41, 0x43))
            get { salt }.isEqualTo("3130")
            get { hashAlg }.isEqualTo(testParameters.hashAlg)
        }
    }

    @ParameterizedTest
    @EnumSource(TestVersions::class)
    fun `should serialize`(testParameters: TestVersions) {
        JsonAssert.assertJsonEquals(
            Hashing(byteArrayOf(0x41, 0x43), "3130", testParameters.hashAlg),
            readResource<Hashing>("message/hashing_${testParameters.version}.json")
        )
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(Hashing::class.java).usingGetClass().verify()
    }
}
