package cz.smarteon.loxone

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LoxoneProfileTest {

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(LoxoneProfile::class.java).verify()
    }

    @Test
    fun `test toString`() {
        expectThat(LoxoneProfile(LoxoneEndpoint("addr", 123), "usr", "pass", "visuPass")) {
            get { toString() }.isEqualTo("usr@addr:123 (unsecured)")
        }
    }
}
