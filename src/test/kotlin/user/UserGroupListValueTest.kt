package cz.smarteon.loxone.user

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class UserGroupListValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(
            UserGroupListValue("[{\"name\":\"Administratoren\",\"description\":\"AdministratorenD\"," +
                    "\"uuid\":\"12eebb90-00a1-3076-ffff88c561c84c44\",\"type\":4,\"userRights\":4294967295\n}]"
            ).userGroups.first()){
            get { uuid.toString() }.isEqualTo("12eebb90-00a1-3076-ffff88c561c84c44")
            get { name }.isEqualTo("Administratoren")
            get { description }.isEqualTo("AdministratorenD")
            get { type }.isEqualTo(UserGroup.UserGroupType.ADMIN)
            get { userRights }.isEqualTo(UserGroup.UserGroupRights.ADMIN)
        }
    }
}
