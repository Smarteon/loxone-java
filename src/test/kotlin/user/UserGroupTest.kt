package cz.smarteon.loxone.user

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.readResource
import cz.smarteon.loxone.user.UserGroup.UserGroupRights
import cz.smarteon.loxone.user.UserGroup.UserGroupType
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class UserGroupTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<UserGroup>("user/group.json")) {
            get { uuid.toString() }.isEqualTo("12eebb90-00a1-3076-ffff88c561c84c44")
            get { name }.isEqualTo("Administratoren")
            get { description }.isEqualTo("AdministratorenD")
            get { type }.isEqualTo(UserGroupType.ADMIN)
            get { userRights }.isEqualTo(UserGroupRights.ADMIN)
        }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            UserGroup(
                LoxoneUuid("12eebb90-00a1-3076-ffff88c561c84c44"),
                "Administratoren",
                "AdministratorenD",
                UserGroupType.ADMIN,
                UserGroupRights.ADMIN),
            readResource<UserGroup>("user/group.json")
        )
    }
}
