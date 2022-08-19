package cz.smarteon.loxone.message

import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isSuccess

class LoxoneMessageTest {

    @Test
    fun `should deserialize get key`() {
        expectThat(readResource("message/getKeyMessage.json", LoxoneMessage::class)) {
            get { control }.isEqualTo("jdev/sys/getkey2/showroom")
            get { code }.isEqualTo(200)
            get { value }.isEqualTo(HASHING)
            get { toString() }
                .contains("jdev/sys/getkey2/showroom")
                .contains("200")
                .contains("key")
                .contains("salt")
        }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            LoxoneMessage("jdev/sys/getkey2/showroom", 200, HASHING),
            readResource("message/getKeyMessage.json", LoxoneMessage::class)
        )
    }

    @Test
    fun `should deserialize alarm all`() {
        expectCatching { readResource("message/alarmAll.json", LoxoneMessage::class) }.isSuccess()
    }

    @Test
    fun `should deserialize getToken failure`() {
        expectThat(readResource("message/getToken401.json", LoxoneMessage::class).code).isEqualTo(401)
    }

    companion object {
        private val HASHING = Hashing(
            "41434633443134324337383441373035453333424344364133373431333430413642333442334244".hexStringToByteArray(),
            "31306137336533622D303163352D313732662D66666666616362383139643462636139",
            "SHA1"
        )

        private fun String.hexStringToByteArray() =
            ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
    }
}
