package cz.smarteon.loxone.user

import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class NfcTagTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<NfcTag>("user/nfc.json")) {
            get { id }.isEqualTo("12 34 56 78 90 98 76 54")
            get { name }.isEqualTo("NFC-Tag")
        }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            NfcTag( "NFC-Tag","12 34 56 78 90 98 76 54"),
            readResource<NfcTag>("user/nfc.json")
        )
    }
}
