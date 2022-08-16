package cz.smarteon.loxone.app

import cz.smarteon.loxone.readValue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TemperatureUnitTest {

    enum class TestTempUnit(
        val value: String,
        val unit: TemperatureUnit
    ) {
        Celsius("0", TemperatureUnit.CELSIUS),
        Fahrenheit("1", TemperatureUnit.FAHRENHEIT);
    }

    @ParameterizedTest
    @EnumSource(TestTempUnit::class)
    fun `should deserialize`(testParameters: TestTempUnit) {
        expectThat(readValue(testParameters.value, TemperatureUnit::class)).isEqualTo(testParameters.unit)
    }
}
