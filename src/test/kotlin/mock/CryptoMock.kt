package cz.smarteon.loxone.mock

import cz.smarteon.loxone.Codec.bytesToHex
import cz.smarteon.loxone.Codec.hexToBytes
import cz.smarteon.loxone.message.Hashing
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.Token
import io.ktor.util.*
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptoMock {

    const val LOXONE_EPOCH_BEGIN = 1230768000

    val SERVER_PRIVATE_KEY: PrivateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec((
        "MIICWgIBAAKBgF/vs3xVxg0T7WO8jVKl8RwrswkGAj+RsVHK49IEb+YA4kPXGx4f" +
        "LnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLKx6FN2dTbMHiydPmTKPFMMQ+Y" +
        "8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iXzWdjFRcLV0bk2yXXAgMBAAEC" +
        "gYAzadB0x7r180H7e2b5bfkDMeAm69N0oe23edYSDVKynrKjzLm5sNhAb8o3tGhw" +
        "95a9J2RqUIEawhjks5Qtyl7q3Q/nXBNk9tcY7PIs+qT7mgy4t+9qKrfgsneOWZQv" +
        "UJ1G0YAn7YMFlpLxEyU+p5Znssf3+p0dGuBxDT+Ryjtd0QJBAKr5dTLL0IfO8GXH" +
        "F15l7RuVrh3SEEeFIAFsz4FqyyMYOcqEehLE8hsc+eXJ4/Nqro92pgETGJfvPPGm" +
        "ZjkhiesCQQCPpTw+0hJi6dEze6WL2R+wCFSvKiM8mBErZ1+nUcZaOZ9s1jSM3eR+" +
        "c24SOU+gjFutGZ87TXykAofTZHfQfwzFAkAe1BYuz5NNOaIdJ/XtvoEvbSDVHbBz" +
        "xOxNdXpBAqmYLWEWRCbixYJGI0ZoCaxBkuXg1mr+XJwdoTSi+fcKrCJ7AkANusVf" +
        "W8TWH3MXcKIKE96rfKBbfbOQfxhlBaRm4bILvaY3SOIM9Mh6LZ4/r6qktcWtbd2C" +
        "VY2sP3GsCtZI31vhAkA9s3YnXfXmlFrLZJpFFQi81JZWuJJEHfHHDwqRo3xFPzat" +
        "YXiOKj/osQWJR9AtxN+10y/MYusRgbMd35BtKWAh").decodeBase64Bytes()))

    val PUBLIC_KEY: ByteArray = (
        "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgF/vs3xVxg0T7WO8jVKl8RwrswkG" +
        "Aj+RsVHK49IEb+YA4kPXGx4fLnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLK" +
        "x6FN2dTbMHiydPmTKPFMMQ+Y8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iX" +
        "zWdjFRcLV0bk2yXXAgMBAAE=").decodeBase64Bytes()

    const val USER = "mocker"
    const val PASS = "pass"
    const val VISU_PASS = "visupass"

    val HASHING = Hashing(
        hexToBytes("41434633443134324337383441373035453333424344364133373431333430413642333442334244"),
        "31306137336533622D303163352D313732662D66666666616362383139643462636139",
        "SHA1"
    )

    val TOKEN = Token(
        "1C368AB5FFB88B964A9F6BE71F27F16E0E42170B",
        hexToBytes("42444546453033423136354538374539393133433435314331333934394244363642364446353633"),
        ((System.currentTimeMillis() / 1000) - LOXONE_EPOCH_BEGIN + 3600).toInt(),
        1666,
        false
    )

    val USER_KEY = LoxoneMessage("jdev/sys/getkey2/$USER", 200, HASHING)
    val USER_VISUSALT = LoxoneMessage("dev/sys/getvisusalt/$USER", 200, HASHING)

    val USER_HASH = computeUserHash(PASS, USER)
    val VISU_HASH = computeUserHash(VISU_PASS)
    val TOKEN_HASH = computeHash(TOKEN.token)


    private fun computeUserHash(pass: String, user: String? = null): String {
        val pwHash = bytesToHex(MessageDigest.getInstance("SHA-1")
                .digest("$pass:${HASHING.salt}".toByteArray())).uppercase()
        val toHash = user?.let { "$it:$pwHash" } ?: pwHash
        return computeHash(toHash)
    }

    private fun computeHash(secret: String): String {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(HASHING.key, "HmacSHA1"))
        return bytesToHex(mac.doFinal(secret.toByteArray()))
    }
}
