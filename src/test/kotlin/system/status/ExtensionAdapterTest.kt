package cz.smarteon.loxone.system.status

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThrows
import strikt.assertions.isA

class ExtensionAdapterTest {

    private val adapter = ExtensionAdapter()

    @Test
    fun `should unmarshall unrecognized`() {
        expect {
            that(adapter.unmarshal(Extension())).isA<UnrecognizedExtension>()
            that(adapter.unmarshal(Extension().apply { name = "Who Knows" })).isA<UnrecognizedExtension>()
        }
    }

    @Test
    fun `marshall not supported`() {
        expectThrows<UnsupportedOperationException> { adapter.marshal(BasicExtension(Extension())) }
    }
}
