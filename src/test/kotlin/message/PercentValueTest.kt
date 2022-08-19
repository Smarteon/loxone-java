package cz.smarteon.loxone.message

import cz.smarteon.loxone.readValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PercentValueTest {

    enum class TestValue(
        val value: String,
        val expected: Int
    ) {
        _99("\"99%\"", 99),
        Empty("\"\"", -1);
    }

    @ParameterizedTest
    @EnumSource(TestValue::class)
    fun `should deserialize`(testParameters: TestValue) {
        expectThat(readValue(testParameters.value, PercentValue::class).value).isEqualTo(testParameters.expected)
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(PercentValue::class.java).usingGetClass().verify()
    }
}
