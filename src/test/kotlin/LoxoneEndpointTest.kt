package cz.smarteon.loxone

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure

class LoxoneEndpointTest {

    enum class TestAddress(
        val address: String,
        val expectedAddress: String
    ) {
        ProxyAddress("dns.loxonecloud.com/50r6f1a0565d3e", "https://dns.loxonecloud.com/50r6f1a0565d3e"),
        ProxyAddressMultiple("dns.loxonecloud.com/50r6f1a0565d3e/5d5d/4se", "https://dns.loxonecloud.com/50r6f1a0565d3e/5d5d/4se"),
        LocalAddress("192.168.88.77", "http://192.168.88.77:80")
    }

    @ParameterizedTest
    @EnumSource(TestAddress::class)
    fun `should create address`(testParameters: TestAddress) {
        val endpoint = LoxoneEndpoint(testParameters.address)
        expectThat(endpoint.httpUrl("").toString()).isEqualTo(testParameters.expectedAddress)
    }

    enum class TestEndpoint(
        val endpoint: LoxoneEndpoint,
        val expectedWs: String,
        val expectedHttp: String
    ) {
        Defaults(LoxoneEndpoint("testAddr"), "ws://testAddr:80/ws/rfc6455", "http://testAddr:80/c"),
        Port(LoxoneEndpoint("testAddr", 34), "ws://testAddr:34/ws/rfc6455", "http://testAddr:34/c"),
        NoSsl(LoxoneEndpoint("testAddr", 34, false), "ws://testAddr:34/ws/rfc6455", "http://testAddr:34/c"),
        Ssl(LoxoneEndpoint("testAddr", 34, true), "wss://testAddr:34/ws/rfc6455", "https://testAddr:34/c")
    }

    @ParameterizedTest
    @EnumSource(TestEndpoint::class)
    fun `should test #testCase`(testParameters: TestEndpoint) {
        expectThat(testParameters.endpoint) {
            get { webSocketUri().toString() }.isEqualTo(testParameters.expectedWs)
            get { httpUrl("c").toString() }.isEqualTo(testParameters.expectedHttp)
        }
    }

    enum class TestToString(
        val endpoint: LoxoneEndpoint,
        val toString: String
    ) {
        Unsecured(LoxoneEndpoint("addr", 80, false), "addr:80 (unsecured)"),
        Secured(LoxoneEndpoint("addr", 80, true), "addr:80 (secured)")
    }

    @ParameterizedTest
    @EnumSource(TestToString::class)
    fun `should test toString()`(testParameters: TestToString) {
        expectThat(testParameters.endpoint.toString()).isEqualTo(testParameters.toString)
    }

    @Test
    fun `should fail to create address`() {
        val address = "https://dns.loxonecloud.com/50r6f1a0565d3e"
        expectCatching {
            LoxoneEndpoint(address)
        }.isFailure()
    }

    @Test
    fun `should verify equals`() {
        EqualsVerifier.forClass(LoxoneEndpoint::class.java).withNonnullFields("host", "path").verify()
    }
}
