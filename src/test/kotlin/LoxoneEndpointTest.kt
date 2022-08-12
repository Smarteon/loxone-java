package cz.smarteon.loxone

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure

class LoxoneEndpointTest {

    enum class TestParameters(
        val address: String,
        val expectedAddress: String
    ) {
        ProxyAddress("dns.loxonecloud.com/50r6f1a0565d3e", "https://dns.loxonecloud.com/50r6f1a0565d3e"),
        ProxyAddressMultiple("dns.loxonecloud.com/50r6f1a0565d3e/5d5d/4se", "https://dns.loxonecloud.com/50r6f1a0565d3e/5d5d/4se"),
        LocalAddress("192.168.88.77", "http://192.168.88.77:80/"),
    }
    @ParameterizedTest
    @EnumSource(TestParameters::class)
    fun `should create address`(testParameters: TestParameters){
        val endpoint = LoxoneEndpoint(testParameters.address)
        expectThat(endpoint.httpUrl("").toString()).isEqualTo(testParameters.expectedAddress)
    }

    @Test
    fun `should fail to create address`(){
        val address = "https://dns.loxonecloud.com/50r6f1a0565d3e"
        expectCatching {
            LoxoneEndpoint(address)
        }.isFailure()
    }
}
