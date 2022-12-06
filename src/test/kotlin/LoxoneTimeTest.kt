package cz.smarteon.loxone

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant
import java.time.ZoneOffset

class LoxoneTimeTest {

    @Test
    fun `should get unix from loxone`() {
        val unixSeconds = Instant.now().epochSecond
        val loxSeconds = unixSeconds - LoxoneTime.LOXONE_EPOCH_BEGIN
        expectThat(LoxoneTime.getUnixEpochSeconds(loxSeconds)).isEqualTo(unixSeconds)
    }

    @Test
    fun `should get local date and time from loxone`() {
        val unixSeconds = Instant.now().epochSecond
        val loxSeconds = unixSeconds - LoxoneTime.LOXONE_EPOCH_BEGIN
        expectThat(LoxoneTime.getLocalDateTime(loxSeconds))
            .get { toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) }.isEqualTo(unixSeconds)
    }
}
