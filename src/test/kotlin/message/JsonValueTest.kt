package cz.smarteon.loxone.message

import com.fasterxml.jackson.databind.node.TextNode
import cz.smarteon.loxone.LoxoneException
import cz.smarteon.loxone.readValue
import cz.smarteon.loxone.writeValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isSuccess

class JsonValueTest {

    @ParameterizedTest
    @ValueSource(strings = ["\"\"", "123", "{}", "[null, -6]", "{\"a\":null,\"b\":34.5}"])
    fun `should deserialize`(value: String) {
        expectCatching { readValue(value, JsonValue::class) }.isSuccess()
    }

    @Test
    fun `should serialize`() {
        expectThat(writeValue(JsonValue(TextNode("haha")))).isEqualTo("\"haha\"")
    }

    @Test
    fun `should convert to primitives`() {
        expectThat(readValue("\"20\"", JsonValue::class).`as`(IntValue::class.java)).isA<IntValue>()
        expectThat(readValue("\"220283340\"", JsonValue::class).`as`((IntValue::class.java))).isA<IntValue>()
    }

    @Test
    fun `should not convert incompatible`() {
        expectThrows<LoxoneException> { JsonValue(TextNode.valueOf("notInt")).`as`(IntValue::class.java) }
    }
}
