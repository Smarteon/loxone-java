package cz.smarteon.loxone

import cz.smarteon.loxone.message.MessageKind
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MessageKindTest {

    @ParameterizedTest
    @EnumSource(MessageKind::class)
    fun `should test valueOf()`(testParameters: MessageKind) {
        val byteVal = testParameters.ordinal.toByte()

        expectThat(MessageKind.valueOf(byteVal)).isEqualTo(testParameters)
    }
}
