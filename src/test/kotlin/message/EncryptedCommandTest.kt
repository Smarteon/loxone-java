package cz.smarteon.loxone.message

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class EncryptedCommandTest {

    @Test
    fun `should encrypt`() {
        expectThat(EncryptedCommand("myCmd%", Nothing::class.java) { c -> "ENCRYPTED->$c" }) {
            get { command }.isEqualTo("jdev/sys/enc/ENCRYPTED-%3EmyCmd%25")
        }
    }

    @Test
    fun `should create gettoken`() {
        expectThat(EncryptedCommand.getToken("testHash", "testUser", TokenPermissionType.APP,
            "testUuid", "testClient") { c -> "ENCRYPTED->$c" }) {
            get { command }.isEqualTo("jdev/sys/enc/ENCRYPTED-%3Ejdev%2Fsys%2Fgettoken%2FtestHash%2FtestUser%2F4%2FtestUuid%2FtestClient")
        }
    }
}
