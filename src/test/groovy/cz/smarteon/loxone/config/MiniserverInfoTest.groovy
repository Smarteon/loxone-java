package cz.smarteon.loxone.config

import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class MiniserverInfoTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        MiniserverInfo info = readResource('/config/miniserverInfo.json', MiniserverInfo)

        then:
        info.serialNumber == '504F9410B84A'
        info.name == 'ShowRoom'
        info.projectName == 'ShowRoom project'
        info.localUrl == '192.168.88.246'
        info.remoteUrl == 'dns.loxonecloud.com/504F9410B84A'
        info.temperatureUnit == TemperatureUnit.CELSIUS
        info.currency == '€'
        info.squareMeasure == 'm²'
        info.location == 'Brno'
        info.categoryTitle == 'Kategorie'
        info.roomTitle == 'Místnost'
        info.type == MiniserverType.GO
        !info.shouldSortByRating()
        info.currentUser?.name == 'showroom'
    }
}
