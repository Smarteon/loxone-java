package cz.smarteon.loxone.system.status

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThrows
import strikt.assertions.isA

class TreeDeviceAdapterTest {

    private val adapter = TreeDeviceAdapter()

    @Test
    fun `should unmarshall`() {
        expect {
            that(adapter.unmarshal(TreeDeviceBase()))
                .isA<TreeDevice>()
                .not().isA<TreeToAirBridge>()
            that(adapter.unmarshal(TreeDeviceBase().apply { airDevices = emptyList() })).isA<TreeToAirBridge>()
        }
    }

    @Test
    fun `marshall not supported`() {
        expectThrows<UnsupportedOperationException> { adapter.marshal(TreeDevice(TreeDeviceBase())) }
    }
}
