package cz.smarteon.loxone.user

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.isLoxoneUuid
import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.util.*

class UserTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<User>("user/user.json")) {
            get { uuid }.isLoxoneUuid("12eebb90-00a1-3073-ffff88c561c84c44")
            get { name }.isEqualTo("admin")
            get { validFrom }.isEqualTo(130)
            get { validUntil }.isEqualTo(1200)
            get { userState }.isEqualTo(UserBase.UserState.ENABLED_UNTIL)
            get { userGroups?.map { it.uuid.toString() } }.isNotNull().containsExactly("12eebb90-00a1-3076-ffff88c561c84c44","12eebb90-00a1-307c-ffff88c561c84c44")
            get { nfcTags?.map { it.id } }.isNotNull().containsExactly("12 34 56 78 90 98 76 54")

            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Prague"))
            get { validFromDateTime.toString() }.isEqualTo("2009-01-01T01:02:10")
            get { validUntilDateTime.toString() }.isEqualTo("2009-01-01T01:20")
        }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            User(LoxoneUuid("12eebb90-00a1-3073-ffff88c561c84c44"), "admin"),
            readResource<User>("user/user2.json")
        )
    }
}
