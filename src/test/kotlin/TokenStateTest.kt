package cz.smarteon.loxone

import cz.smarteon.loxone.message.Token
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class TokenStateTest {

    @ParameterizedTest(name = "test expiration of {0} token")
    @EnumSource(Expiration::class)
    fun `should evaluate expiration`(expiration: Expiration) {
        expectThat(TokenState(expiration.token)) {
            get { isUsable }.isEqualTo(expiration.usable)
            get { isExpired }.isEqualTo(expiration.expired)
            get { needsRefresh() }.isEqualTo(expiration.refresh)
            get { secondsToRefresh() }.isEqualTo(expiration.secsToRefresh)
        }
    }

    enum class Expiration(
        val token: Token? = null,
        val usable: Boolean = false,
        val expired: Boolean,
        val refresh: Boolean = false,
        val secsToRefresh: Long = 0
    ) {
        VALID(tokenWithExpire(3600), true, false, false, 3300),
        OLD(tokenWithExpire(-5), false, true, false),
        EXPIRED(tokenWithExpire(5), false, true, false),
        TO_REFRESH(tokenWithExpire(65), false, false, true),
        NOT_FILLED(tokenWithExpire(filled = false), expired = true),
        NULL(expired = true);
    }



}

private fun tokenWithExpire(expire: Long = 0, filled: Boolean = true) = mockk<Token> {
    every { secondsToExpire }.returns(expire)
    every { isFilled }.returns(filled)
}
