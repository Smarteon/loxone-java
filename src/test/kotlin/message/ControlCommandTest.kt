package cz.smarteon.loxone.message

import cz.smarteon.loxone.app.MiniserverType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ControlCommandTest {

    @Test
    fun `should test basic control command`() {
        expectThat(ControlCommand("uuid", "op", IntValue::class.java, MiniserverType.KNOWN)) {
            get { command }.isEqualTo("jdev/sps/io/uuid/op")
            get { controlCommand }.isEqualTo("uuid/op")
            get { supportedMiniservers }.isEqualTo(MiniserverType.KNOWN)
        }
    }
}
