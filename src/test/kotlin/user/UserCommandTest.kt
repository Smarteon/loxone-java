package cz.smarteon.loxone.user

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.readResource
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class UserCommandTest {

    @Test
    fun `should create commands`() {
        val nfcTag = NfcTag(NFC_NAME, NFC_ID)
        val userGroup = UserGroup(LoxoneUuid(GROUP_U))
        val user = readResource<User>("user/user.json")

        expect {
            that(UserCommand.createUser(user).command).isEqualTo("jdev/sps/createuser/admin")
            that(UserCommand.addOrEditUser(user).command).isEqualTo("jdev/sps/addoredituser/" +
                    "{\"uuid\":\"12eebb90-00a1-3073-ffff88c561c84c44\",\"name\":\"admin\",\"validUntil\":1200,\"validF" +
                    "rom\":130,\"userState\":2,\"usergroups\":[\"12eebb90-00a1-3076-ffff88c561c84c44\"" +
                    ",\"12eebb90-00a1-307c-ffff88c561c84c44\"],\"nfcTags\":[\"12 34 56 78 90 98 76 54\"]" +
                    ",\"userid\":\"1234 24 12 83\",\"admin\":true}")
            that(UserCommand.getUsers().command).isEqualTo("jdev/sps/getuserlist2/")
            that(UserCommand.getUserDetails(user).command).isEqualTo("jdev/sps/getuser/$USER_U")
            that(UserCommand.getUserGroups().command).isEqualTo("jdev/sps/getgrouplist/")
            that(UserCommand.addUserToGroup(user, userGroup).command).isEqualTo("jdev/sps/assignusertogroup/$USER_U/$GROUP_U")
            that(UserCommand.removeUserFromGroup(user, userGroup).command).isEqualTo("jdev/sps/removeuserfromgroup/$USER_U/$GROUP_U")
            that(UserCommand.addNfcToUser(user, nfcTag).command).isEqualTo("jdev/sps/addusernfc/$USER_U/$NFC_ID/$NFC_NAME")
            that(UserCommand.removeNfcFromUser(user, nfcTag).command).isEqualTo("jdev/sps/removeusernfc/$USER_U/$NFC_ID")
        }

        val testUser = User(LoxoneUuid("12eebb90-00a1-3073-ffff88c561c84c44"))
        expectThat(UserCommand.deleteUser(testUser).command).isEqualTo("jdev/sps/deleteuser/12eebb90-00a1-3073-ffff88c561c84c44")
    }

    @Test
    fun `should create invalid command`() {
        val invalidUser = User(LoxoneUuid("12eebb90-00a1-3073-ffff88c561c84c44"))
        expectThrows<NullPointerException> { UserCommand.createUser(invalidUser) }
    }

    companion object {
        private const val USER_U = "12eebb90-00a1-3073-ffff88c561c84c44"
        private const val GROUP_U = "13eebb90-00a1-3073-ffff88c561c84c44"
        private const val NFC_ID = "1234"
        private const val NFC_NAME = "testTag"
    }
}
