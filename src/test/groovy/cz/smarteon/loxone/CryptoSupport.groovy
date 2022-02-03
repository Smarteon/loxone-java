package cz.smarteon.loxone

import cz.smarteon.loxone.message.Hashing
import cz.smarteon.loxone.message.Token

abstract class CryptoSupport {

    static final int LOXONE_EPOCH_BEGIN = 1230768000

    static final byte[] PUBLIC_KEY = (
            'MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgF/vs3xVxg0T7WO8jVKl8RwrswkG\n' +
                    'Aj+RsVHK49IEb+YA4kPXGx4fLnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLK\n' +
                    'x6FN2dTbMHiydPmTKPFMMQ+Y8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iX\n' +
                    'zWdjFRcLV0bk2yXXAgMBAAE=').decodeBase64()

    static final String USER = 'mocker'
    static final String PASS = 'pass'
    static final String VISU_PASS = 'visupass'

    static final Hashing HASHING = new Hashing(
            '41434633443134324337383441373035453333424344364133373431333430413642333442334244'.decodeHex(),
            '31306137336533622D303163352D313732662D66666666616362383139643462636139', 'SHA1')

    static final Token TOKEN = new Token(
            '1C368AB5FFB88B964A9F6BE71F27F16E0E42170B',
            '42444546453033423136354538374539393133433435314331333934394244363642364446353633'.decodeHex(),
            ((System.currentTimeMillis() / 1000) - LOXONE_EPOCH_BEGIN + 3600).toInteger(), 1666, false)

}
