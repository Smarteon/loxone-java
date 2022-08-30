package cz.smarteon.loxone.app

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DigitalInfoControlTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<DigitalInfoControl>("app/digitalInfoControl.json")) {
            get { uuid }.isLoxoneUuid("192cc0b1-0168-ec92-ffffc0f606ef595c")
            get { name }.isEqualTo("kitchen-SqueezeSound")
            get { stateActive() }.isLoxoneUuid("192cc0b1-0168-ec92-ffffc0f606ef595c")
        }
    }
}
