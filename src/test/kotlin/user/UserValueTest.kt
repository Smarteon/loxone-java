package cz.smarteon.loxone.user

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class UserValueTest {

    @Test
    fun `should deserialize`() {
        expectThat(
            UserValue("{\"name\": \"admin\",\"uuid\": \"12eebb90-00a1-3073-ffff88c561c84c44\"}").user){
            get { uuid.toString() }.isEqualTo("12eebb90-00a1-3073-ffff88c561c84c44")
            get { name }.isEqualTo("admin")
        }
    }
}
