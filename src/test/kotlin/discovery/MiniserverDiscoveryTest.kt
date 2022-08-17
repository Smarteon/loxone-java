package cz.smarteon.loxone.discovery

import cz.smarteon.loxone.LoxoneEndpoint
import cz.smarteon.loxone.app.MiniserverType
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDateTime

class MiniserverDiscoveryTest {

    @Test
    fun `should parse`() {
        expectThat(
            MiniserverDiscovery.fromResponse(
                "LoxLIVE: baf 192.168.5.1:80 504F94112134 12.0.2.24 Prog:2021-06-01 23:40:59 Type:0 " +
                        "HwId:A0000 IPv6:,00000000:0/X,13106567:11021110/O,0cd87947:11010825/O,05d8726f:12010322/O"
            )
        ) {
            get { name }.isEqualTo("baf")
            get { address }.isEqualTo("192.168.5.1")
            get { port }.isEqualTo(80)
            get { mac }.isEqualTo("504F94112134")
            get { firmwareVersion }.isEqualTo("12.0.2.24")
            get { type }.isEqualTo(MiniserverType.REGULAR)
            get { lastConfig }.isEqualTo(LocalDateTime.of(2021, 6, 1, 23, 40, 59))
            get { loxoneEndpoint }.isEqualTo(LoxoneEndpoint("192.168.5.1", 80, false))
        }
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(MiniserverDiscovery::class.java).verify()
    }
}
