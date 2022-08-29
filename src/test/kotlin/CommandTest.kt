package cz.smarteon.loxone

import cz.smarteon.loxone.Command.voidWsCommand
import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.message.LongValue
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class CommandTest {

    @Test
    fun `test voidWsCommand`() {
        expectThat(voidWsCommand(MiniserverType.KNOWN, "CMD/%s%s", "aa", "bb")) {
            get { command }.isEqualTo("CMD/aabb")
            get { shouldContain }.isEqualTo("CMD/aabb")
        }
    }

    @Test
    fun `test json command`() {
        expectThat(Command("jdev/test", Command.Type.JSON, String::class.java, false, true, MiniserverType.FIRST_GEN)) {
            get { command }.isEqualTo("jdev/test")
            get { shouldContain }.isEqualTo("dev/test")
            get { supportedMiniservers }.isEqualTo(MiniserverType.FIRST_GEN)
            get { supportsMiniserver(MiniserverType.REGULAR_V2) }.isFalse()
            get { `is`("dev/test/something") }.isTrue()
            get { `is`("something") }.isFalse()
        }
    }

    @Test
    fun `should ensure response`() {
        val cmd = Command("jdev/test", Command.Type.JSON, String::class.java, false, true, MiniserverType.KNOWN)

        expectThat(cmd.ensureResponse("aa")).isEqualTo("aa")
        expectThrows<LoxoneException> { cmd.ensureResponse(null) }
        expectThrows<LoxoneException> { cmd.ensureResponse(3) }
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(LongValue::class.java).usingGetClass().verify()
    }
}
