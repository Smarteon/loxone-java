package cz.smarteon.loxone.app

import cz.smarteon.loxone.readValue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MiniserverTypeTest {

    enum class TestMiniserverType(
        val value: String,
        val type: MiniserverType
    ) {
        Regular("0", MiniserverType.REGULAR),
        Go("1", MiniserverType.GO),
        RegularV2("2", MiniserverType.REGULAR_V2),
        UnknownType3("3", MiniserverType.UNKNOWN),
        UnknownType5("5", MiniserverType.UNKNOWN);
    }

    @ParameterizedTest
    @EnumSource(TestMiniserverType::class)
    fun `should deserialize`(testParameters: TestMiniserverType) {
        expectThat(readValue(testParameters.value, MiniserverType::class)).isEqualTo(testParameters.type)
    }
}
