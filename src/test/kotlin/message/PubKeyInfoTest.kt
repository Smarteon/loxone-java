package cz.smarteon.loxone.message

import cz.smarteon.loxone.readValue
import cz.smarteon.loxone.writeValue
import io.ktor.util.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PubKeyInfoTest {

    @Test
    fun `should deserialize`() {
        expectThat(readValue(PUB_KEY_JSON, PubKeyInfo::class).pubKey).isEqualTo(PUB_KEY_STRING.decodeBase64Bytes())
    }

    @Test
    fun `should serialize`() {
        expectThat(writeValue(PubKeyInfo(PUB_KEY_STRING.decodeBase64Bytes()))).isEqualTo(PUB_KEY_JSON)
    }

    companion object {
        private const val PUB_KEY_STRING = "MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAb1jLHZz08OdFf61dpQPFKWJjt8jdb1o3" +
                "cbYOT5XTjG+BdceLdSrlEMG3iFL9NQ+d7xKYwLLnoPVNMEr/ZnA1/wIDAQAB"
        private const val PUB_KEY_JSON = "\"-----BEGIN CERTIFICATE-----$PUB_KEY_STRING-----END CERTIFICATE-----\""
    }
}
