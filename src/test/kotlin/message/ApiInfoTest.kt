package cz.smarteon.loxone.message

import cz.smarteon.loxone.readResource
import net.javacrumbs.jsonunit.JsonAssert
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class ApiInfoTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<ApiInfo>("message/apiInfo.json")) {
            get { mac }.isEqualTo("50:4F:94:10:B8:4A")
            get { version }.isEqualTo("9.1.10.30")
        }
    }

    @Test
    fun `should deserialize version 10_3`() {
        expectThat(readResource<ApiInfo>("message/apiInfo103.json")) {
            get { mac }.isEqualTo("EE:E0:00:D8:0B:0E")
            get { version }.isEqualTo("10.3.11.25")
            get { eventSlots }.isTrue()
        }
    }

    @Test
    fun `should serialize`() {
        JsonAssert.assertJsonEquals(
            ApiInfo("50:4F:94:10:B8:4A", "9.1.10.30"),
            readResource<ApiInfo>("message/apiInfo.json")
        )
    }
}
