package cz.smarteon.loxone.message

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TokenPermissionTypeTest {

    enum class TestType(
        val type: TokenPermissionType,
        val id: Int
    ) {
        WEB(TokenPermissionType.WEB, 2),
        APP(TokenPermissionType.APP, 4);
    }

    @ParameterizedTest
    @EnumSource(TestType::class)
    fun `should deserialize`(testParameters: TestType) {
        expectThat(testParameters.type.id).isEqualTo(testParameters.id)
    }
}
