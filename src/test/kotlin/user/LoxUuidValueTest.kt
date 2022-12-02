package cz.smarteon.loxone.user

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.readValue
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LoxUuidValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue<LoxUuidValue>("\"1a2c6b41-019d-7036-ffff504f94a03d3e\"").uuid).isEqualTo(
            LoxoneUuid("1a2c6b41-019d-7036-ffff504f94a03d3e")
        )
    }
}
