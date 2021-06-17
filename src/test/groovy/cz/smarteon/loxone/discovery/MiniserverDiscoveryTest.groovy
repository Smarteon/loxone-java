package cz.smarteon.loxone.discovery

import cz.smarteon.loxone.LoxoneEndpoint
import cz.smarteon.loxone.app.MiniserverType
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import java.time.LocalDateTime

class MiniserverDiscoveryTest extends Specification {

    def "should parse"() {
        when:
        MiniserverDiscovery discovery = MiniserverDiscovery.fromResponse(
                'LoxLIVE: baf 192.168.5.1:80 504F94112134 12.0.2.24 Prog:2021-06-01 23:40:59 Type:0 ' +
                        'HwId:A0000 IPv6:,00000000:0/X,13106567:11021110/O,0cd87947:11010825/O,05d8726f:12010322/O')

        then:
        discovery
        discovery.name == 'baf'
        discovery.address == '192.168.5.1'
        discovery.port == 80
        discovery.mac == '504F94112134'
        discovery.firmwareVersion == '12.0.2.24'
        discovery.type == MiniserverType.REGULAR
        discovery.lastConfig == LocalDateTime.of(2021, 6, 1, 23, 40, 59)

        discovery.loxoneEndpoint == new LoxoneEndpoint('192.168.5.1', 80, false)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(MiniserverDiscovery.class).verify()
    }
}
