package cz.smarteon.loxone

import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.message.ControlCommand.genericControlCommand
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SecuredCommandTest {

    @Test
    fun `test voidWsCommand`() {
        expectThat(SecuredCommand(genericControlCommand("uuid", "op", MiniserverType.FIRST_GEN), "visuhash")) {
            get { command }.isEqualTo("jdev/sps/ios/visuhash/uuid/op")
            get { supportedMiniservers }.isEqualTo(MiniserverType.FIRST_GEN)
        }
    }
}
