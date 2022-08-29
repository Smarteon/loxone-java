package cz.smarteon.loxone.app

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class AnalogInfoControlTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource("app/analogInfoControl.json", AnalogInfoControl::class)) {
            get { uuid }.isLoxoneUuid("192cdcb5-0250-2a43-ffffc0f606ef595c")
            get { name }.isEqualTo("kitchen-SqueezeTrack")
            get { stateValue() }.isLoxoneUuid("192cdcb5-0250-2a43-ffffc0f606ef595c")
            get { stateError() }.isLoxoneUuid("192cdcb5-0250-2a42-ffff6f2343a00358")
        }
    }
}
