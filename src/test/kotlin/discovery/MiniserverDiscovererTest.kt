package cz.smarteon.loxone.discovery

import cz.smarteon.loxone.mock.MockDiscovery
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty
import strikt.assertions.map

class MiniserverDiscovererTest {

    private val discoveryMock = MockDiscovery()

    private lateinit var discoverer: MiniserverDiscoverer

    @BeforeEach
    fun setup() {
        discoveryMock.start()
        discoverer = MiniserverDiscoverer(discoveryMock.listenPort, discoveryMock.answerPort)
    }

    @AfterEach
    fun cleanup() {
        discoveryMock.stop()
    }

    @Test
    fun `should discover`() {
        expectThat(discoverer.discover(1, 1000)).map { it.name }.containsExactly(MockDiscovery.SERVER_NAME)
    }

    @Test
    fun `finish when there is nothing to discover`() {
        discoveryMock.stop()
        expectThat(discoverer.discover(1, 500)).isEmpty()
    }
}
