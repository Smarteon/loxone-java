package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DigitalInfoControlTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource("app/digitalInfoControl.json", DigitalInfoControl::class)) {
            get { uuid }.isEqualTo(LoxoneUuid("192cc0b1-0168-ec92-ffffc0f606ef595c"))
            get { name }.isEqualTo("kitchen-SqueezeSound")
            get { stateActive() }.isEqualTo(LoxoneUuid("192cc0b1-0168-ec92-ffffc0f606ef595c"))
        }
    }
}
