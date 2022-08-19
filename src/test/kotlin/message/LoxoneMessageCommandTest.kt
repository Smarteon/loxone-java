package cz.smarteon.loxone.message

import cz.smarteon.loxone.Command
import cz.smarteon.loxone.LoxoneException
import cz.smarteon.loxone.app.MiniserverType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class LoxoneMessageCommandTest {

    @Test
    fun `should ensure value`() {
        val cmd = LoxoneMessageCommand(
            "jdev/test",
            Command.Type.JSON,
            IntValue::class.java,
            false,
            true,
            MiniserverType.KNOWN
        )

        expectThat(cmd).get { ensureValue(IntValue(5)).value }.isEqualTo(5)

        expectThrows<LoxoneException> { cmd.ensureValue(null) }
        expectThrows<LoxoneException> { cmd.ensureValue(LongValue("0")) }
    }
}
