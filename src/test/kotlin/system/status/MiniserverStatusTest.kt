package cz.smarteon.loxone.system.status

import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.readResourceXml
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*

class MiniserverStatusTest {

    @Test
    fun `should deserialize`() {
        val ms = readResourceXml<MiniserverStatus>("system/status/status.xml")

        expectThat(ms) {
            get { modified }.isEqualTo("2019-09-04 13:02:36")
            get { type }.isEqualTo(MiniserverType.REGULAR)
            get { name }.isEqualTo("test")
            get { ip }.isEqualTo("192.168.17.17")
            get { mask }.isEqualTo("255.255.255.0")
            get { gateway }.isEqualTo("192.168.17.1")
            get { usesDhcp() }.isTrue()
            get { dns1 }.isEqualTo("192.168.17.1")
            get { dns2 }.isEqualTo("8.8.8.8")
            get { mac }.isEqualTo("504F9411234")
            get { device }.isEqualTo("TestDevice")
            get { version }.isEqualTo("10.2.3.26")
            get { lanErrorsPercent }.isEqualTo(0.0)
            get { linkErrorsCount }.isEqualTo(0)

            get { extensions }.hasSize(12)
            get { extensions[1] }
                .isA<BasicExtension>()
                .get { name }.isEqualTo("Extension")
            get { extensions[2] }.and {
                get { name }.isEqualTo("RS485 Extension")
                get { online }.isTrue()
            }
            get { extensions[4] }.and {
                get { online }.isFalse()
                get { name }.isEqualTo("DMX Extension")
            }
            get { extensions[5] }.and {
                get { online }.isTrue()
                get { name }.isEqualTo("Dali Extension")
            }
            get { extensions[6] }.and {
                get { online }.isTrue()
                get { name }.isEqualTo("DI Extension")
            }
            get { extensions[7] }.and {
                get { online }.isTrue()
                get { name }.isEqualTo("Modbus Extension")
            }
            get { extensions[8] }.and {
                get { online }.isTrue()
                get { name }.isEqualTo("Dimmer Extension")
            }
            get { extensions[9].serialNumber }.isEqualTo("17d8060c")
            get { extensions[10].serialNumber }.isEqualTo("16d80173")
            get { extensions[11] }
                .isA<UnrecognizedExtension>()
                .get { name }.isEqualTo("Some Future Extension")
        }

        val airBaseExtensions = ms.getExtensions(AirBaseExtension::class.java)
        expectThat(airBaseExtensions).hasSize(1)

        expectThat(airBaseExtensions[0]) {
            get { devices }.hasSize(3)
            get { devices[0].name }.isEqualTo("VentilLeft")
            get { devices[2].name }.isEqualTo("Multi Extension Air")
            get { updating }.isTrue()
            get { updateProgress }.isEqualTo(89)
            get { hwVersion }.isEqualTo("2")
        }

        expectThat(airBaseExtensions[0].devices[2] as MultiExtensionAir) {
            get { devices }.hasSize(3)
            get { serialForOneWireDetails }.isEqualTo("fe894bc6")
        }

        val treeExtensions = ms.getExtensions(TreeExtension::class.java)
        expectThat(treeExtensions) {
            hasSize(1)
            get { this[0].leftBranch?.devices?.size }.isEqualTo(2)
            get { this[0].rightBranch?.devices?.get(0)?.name }.isEqualTo("Linka")
            get { this[0].rightBranch?.devices?.get(1)?.name }.isEqualTo("Tree to Air Bridge")
            get { this[0].leftBranch?.devices?.get(1)?.updating }.isTrue()
            get { this[0].leftBranch?.devices?.get(1)?.updateProgress }.isEqualTo(73)
        }

        val tree2airBridge = treeExtensions[0].rightBranch?.devices?.get(1) as TreeToAirBridge
        expectThat(tree2airBridge) {
            get { devices }.hasSize(1)
            get { hwVersion }.isEqualTo("100")
            get { mac }.isEqualTo("50:4F:94:FF:FE:C0:07:95")
            get { occupied }.isFalse()
            get { interfered }.isFalse()
            get { tree2airBridge.devices[0].updating }.isTrue()
            get { tree2airBridge.devices[0].updateProgress }.isEqualTo(57)
        }

        expectThat(ms.getExtensions(DaliExtension::class.java)) {
            hasSize(1)
            get { this[0].devices }.hasSize(1)
            get { this[0].devices[0].serialNumber }.isEqualTo("0:83213221")
        }
    }
}
