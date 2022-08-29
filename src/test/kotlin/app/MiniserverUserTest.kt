package cz.smarteon.loxone.app

import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class MiniserverUserTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<MiniserverUser>("app/miniserverUser.json")) {
            get { name }.isEqualTo("showroom")
            get { uuid }.isLoxoneUuid("0f86a25d-026f-1c1e-ffffd4c75dbaf53c")
            get { isAdmin }.isTrue()
            get { canChangePassword() }.isTrue()
            get { rights }.isEqualTo(RIGHTS.toBigInteger())
        }
    }

    companion object {
        private const val RIGHTS = 4294967295
    }
}
