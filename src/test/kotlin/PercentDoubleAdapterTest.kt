package cz.smarteon.loxone

import cz.smarteon.loxone.PercentDoubleAdapter.stripPercent
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isNull

class PercentDoubleAdapterTest {

    private val percentAdapter = PercentDoubleAdapter()

    @Test
    fun `should strip percent`() {
        expect {
            that(stripPercent("23%")).isEqualTo("23")
            catching { stripPercent(null) }.isFailure().isA<IllegalArgumentException>()
            catching { stripPercent("12") }.isFailure().isA<IllegalArgumentException>()
        }
    }

    @Test
    fun `should unmarshall`() {
        expect {
            that(percentAdapter.unmarshal(null)).isNull()
            that(percentAdapter.unmarshal("")).isEqualTo(-1.0)
            that(percentAdapter.unmarshal("12%")).isEqualTo(12.0)
        }
    }

    @Test
    fun `marshall not supported`() {
        expectThrows<UnsupportedOperationException> { percentAdapter.marshal(12.0) }
    }
}
