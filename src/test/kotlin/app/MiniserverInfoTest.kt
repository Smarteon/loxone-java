package cz.smarteon.loxone.app

import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse

class MiniserverInfoTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource("app/miniserverInfo.json", MiniserverInfo::class)) {
            get { serialNumber }.isEqualTo("504F9410B84A")
            get { name }.isEqualTo("ShowRoom")
            get { projectName }.isEqualTo("ShowRoom project")
            get { localUrl }.isEqualTo("192.168.88.246")
            get { remoteUrl }.isEqualTo("dns.loxonecloud.com/504F9410B84A")
            get { temperatureUnit }.isEqualTo(TemperatureUnit.CELSIUS)
            get { currency }.isEqualTo("€")
            get { squareMeasure }.isEqualTo("m²")
            get { location }.isEqualTo("Brno")
            get { categoryTitle }.isEqualTo("Kategorie")
            get { roomTitle }.isEqualTo("Místnost")
            get { type }.isEqualTo(MiniserverType.GO)
            get { shouldSortByRating() }.isFalse()
            get { currentUser.name }.isEqualTo("showroom")
        }
    }
}
