package cz.smarteon.loxone.mock

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

internal class MockDiscovery {

    val listenPort: Int
        get() = socket.localAddress.toJavaAddress().port

    val answerPort: Int
        get() = listenPort + 1

    private val running = AtomicBoolean(true)

    private lateinit var socket: BoundDatagramSocket
    private lateinit var listenJob: Job

    fun start() {
        socket = aSocket(ActorSelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress(BROADCAST_LISTEN, BIND_RANDOM))

        listenJob = GlobalScope.launch {
            while (running.get()) {
                launch {
                    val datagram = socket.receive()
                    val firstByte = datagram.packet.readByte()
                    if (firstByte == ZERO_BYTE) {
                        socket.send(
                            Datagram(
                                ByteReadPacket(ANSWER),
                                InetSocketAddress(datagram.address.toJavaAddress().hostname, answerPort)
                            )
                        )
                    }
                }
            }
        }
    }

    fun stop() {
        running.set(false)
        socket.close()
        runBlocking {
            socket.awaitClosed()
            listenJob.cancel()
        }
    }

    companion object {
        const val SERVER_NAME = "MockDiscovery"

        private const val BROADCAST_LISTEN = "0.0.0.0"
        private const val BIND_RANDOM = 0
        private const val ZERO_BYTE = 0x00.toByte()
        private val ANSWER = (
            "LoxLIVE: $SERVER_NAME 192.168.5.1:80 504F94112134 12.0.2.24 Prog:2021-06-01 23:40:59 Type:0 " +
                "HwId:A0000 IPv6:,00000000:0/X,13106567:11021110/O,0cd87947:11010825/O,05d8726f:12010322/O"
            ).toByteArray()
    }
}
