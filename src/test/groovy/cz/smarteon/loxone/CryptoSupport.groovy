package cz.smarteon.loxone

import cz.smarteon.loxone.message.Hashing
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.Token

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

abstract class CryptoSupport {

    static final int LOXONE_EPOCH_BEGIN = 1230768000

    static final PrivateKey SERVER_PRIVATE_KEY =
            KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec((
                    'MIICWgIBAAKBgF/vs3xVxg0T7WO8jVKl8RwrswkGAj+RsVHK49IEb+YA4kPXGx4f\n' +
                            'LnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLKx6FN2dTbMHiydPmTKPFMMQ+Y\n' +
                            '8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iXzWdjFRcLV0bk2yXXAgMBAAEC\n' +
                            'gYAzadB0x7r180H7e2b5bfkDMeAm69N0oe23edYSDVKynrKjzLm5sNhAb8o3tGhw\n' +
                            '95a9J2RqUIEawhjks5Qtyl7q3Q/nXBNk9tcY7PIs+qT7mgy4t+9qKrfgsneOWZQv\n' +
                            'UJ1G0YAn7YMFlpLxEyU+p5Znssf3+p0dGuBxDT+Ryjtd0QJBAKr5dTLL0IfO8GXH\n' +
                            'F15l7RuVrh3SEEeFIAFsz4FqyyMYOcqEehLE8hsc+eXJ4/Nqro92pgETGJfvPPGm\n' +
                            'ZjkhiesCQQCPpTw+0hJi6dEze6WL2R+wCFSvKiM8mBErZ1+nUcZaOZ9s1jSM3eR+\n' +
                            'c24SOU+gjFutGZ87TXykAofTZHfQfwzFAkAe1BYuz5NNOaIdJ/XtvoEvbSDVHbBz\n' +
                            'xOxNdXpBAqmYLWEWRCbixYJGI0ZoCaxBkuXg1mr+XJwdoTSi+fcKrCJ7AkANusVf\n' +
                            'W8TWH3MXcKIKE96rfKBbfbOQfxhlBaRm4bILvaY3SOIM9Mh6LZ4/r6qktcWtbd2C\n' +
                            'VY2sP3GsCtZI31vhAkA9s3YnXfXmlFrLZJpFFQi81JZWuJJEHfHHDwqRo3xFPzat\n' +
                            'YXiOKj/osQWJR9AtxN+10y/MYusRgbMd35BtKWAh').decodeBase64()))

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

    static final LoxoneMessage USER_KEY = new LoxoneMessage("jdev/sys/getkey2/$USER", 200, HASHING)
    static final LoxoneMessage USER_VISUSALT = new LoxoneMessage("dev/sys/getvisusalt/$USER", 200, HASHING)

    static final String USER_HASH = computeUserHash(PASS, USER)
    static final String VISU_HASH = computeUserHash(VISU_PASS)



    static String computeUserHash(String pass, String user = null) {
        def pwHash = MessageDigest.getInstance("SHA-1")
                .digest("$pass:$HASHING.salt".bytes).encodeHex().toString().toUpperCase()
        def mac = Mac.getInstance("HmacSHA1")
        mac.init(new SecretKeySpec(HASHING.key, "HmacSHA1"))
        def toHash = user != null ? "$USER:$pwHash" : pwHash
        mac.doFinal(toHash.bytes).encodeHex().toString()
    }
}