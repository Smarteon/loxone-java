package cz.smarteon.loxone.message

import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals
import net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isGreaterThanOrEqualTo
import strikt.assertions.isLessThanOrEqualTo
import strikt.assertions.isTrue
import strikt.java.propertiesAreEqualTo
import strikt.java.time.isAfter
import java.time.Instant
import java.time.ZoneOffset

class TokenTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource("message/token.json", Token::class)).propertiesAreEqualTo(TEST_TOKEN)
    }

    @Test
    fun `should calculate valid until`() {
        val validUntil = 60 + Instant.now().epochSecond - 1230768000
        expectThat(Token(null, null, validUntil.toInt(), 0, true)) {
            get { secondsToExpire }
                .isGreaterThanOrEqualTo(0)
                .isLessThanOrEqualTo(60)
            // remove toInstant call once strikt-jvm starts support LocalDateTime
            get { validUntilDateTime.toInstant(ZoneOffset.UTC) }.isAfter(Instant.now())
        }
    }

    @Test
    fun `should merge`() {
        expectThat(TEST_TOKEN.merge(Token(null, null, 372151839, 1662, true))) {
            get { token }.isEqualTo("8E2AA590E996B321C0E17C3FA9F7A3C17BD376CC")
            get { key }.isEqualTo(byteArrayOf(68, 68, 50))
            get { validUntil }.isEqualTo(372151839)
            get { rights }.isEqualTo(1662)
            get { isUnsecurePassword }.isTrue()
        }
    }

    @Test
    fun `should check filled`() {
        expect {
            that(TEST_TOKEN).get { isFilled }.isTrue()
            that(Token(null, null, 372151839, 1662, true)).get { isFilled }.isFalse()
            that(Token("AA", null, 372151839, 1662, true)).get { isFilled }.isFalse()
            that(Token(null, byteArrayOf(68), 372151839, 1662, true)).get { isFilled }.isFalse()
        }
    }

    @Test
    fun `should serialize`() {
        assertJsonEquals(TEST_TOKEN, resource("message/token.json"))
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(Token::class.java).usingGetClass().verify()
    }

    companion object {
        private val TEST_TOKEN = Token(
            "8E2AA590E996B321C0E17C3FA9F7A3C17BD376CC",
            byteArrayOf(68, 68, 50),
            342151839,
            1666,
            false
        )
    }
}
