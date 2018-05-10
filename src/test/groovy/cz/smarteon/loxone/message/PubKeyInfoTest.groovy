package cz.smarteon.loxone.message

import spock.lang.Specification

class PubKeyInfoTest extends Specification implements SerializationSupport {

    private static final PUB_KEY_STRING = 'MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAb1jLHZz08OdFf61dpQPFKWJjt8jdb1o3' +
            'cbYOT5XTjG+BdceLdSrlEMG3iFL9NQ+d7xKYwLLnoPVNMEr/ZnA1/wIDAQAB'
    private static final PUB_KEY_JSON = "\"-----BEGIN CERTIFICATE-----$PUB_KEY_STRING-----END CERTIFICATE-----\""

    def "should deserialize"() {
        expect:
        readValue(PUB_KEY_JSON, PubKeyInfo).pubKey == PUB_KEY_STRING.decodeBase64()
    }

    def "should serialize"() {
        expect:
        writeValue(new PubKeyInfo(PUB_KEY_STRING.decodeBase64())) == PUB_KEY_JSON
    }
}
