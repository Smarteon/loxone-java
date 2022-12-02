package cz.smarteon.loxone.user

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class UserListValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(
            UserListValue("[{\"name\":\"Administrator\",\"uuid\":\"089396d4-0207-0119-1900000000000000\"," +
                    "\"isAdmin\":true,\"userState\": 0}]").users.first()){
            get { uuid.toString() }.isEqualTo("089396d4-0207-0119-1900000000000000")
            get { name }.isEqualTo("Administrator")
            get { isAdmin }.isTrue()
            get { userState }.isEqualTo(UserBase.UserState.ENABLED)
            get { isRepresentsControl }.isFalse()
        }
    }
}
