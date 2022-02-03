package cz.smarteon.loxone.mock

import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import cz.smarteon.loxone.Codec.hexToBytes
import cz.smarteon.loxone.Codec.writeMessage
import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.JsonValue
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API
import cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY
import cz.smarteon.loxone.message.LoxoneValue
import cz.smarteon.loxone.message.PubKeyInfo
import cz.smarteon.loxone.mock.CryptoMock.PUBLIC_KEY
import cz.smarteon.loxone.mock.CryptoMock.SERVER_PRIVATE_KEY
import cz.smarteon.loxone.mock.CryptoMock.TOKEN
import cz.smarteon.loxone.mock.CryptoMock.USER
import cz.smarteon.loxone.mock.CryptoMock.USER_HASH
import cz.smarteon.loxone.mock.CryptoMock.USER_KEY
import cz.smarteon.loxone.mock.CryptoMock.USER_VISUSALT
import cz.smarteon.loxone.mock.CryptoMock.VISU_HASH
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.Matcher
import java.net.URLDecoder
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class MockMiniserver {

    val port: Int by lazy { runBlocking { server.resolvedConnectors().first().port } }

    private lateinit var sharedKey: SecretKey
    private lateinit var sharedKeyIv: ByteArray

    private var server: ApplicationEngine = createServer()

    private val msgProcessors = listOf(
        ::processKeyExchange,
        ::processGetKey,
        ::processEncrypted,
        ::processGetToken,
        ::processGetUserSalt,
        ::processSecured,
        ::processEnableUpdates,
        ::processStubbing
    )

    var badCredentials = 0
    private val stubbing = Stubbing()

    private fun createServer(port: Int = 0) = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            jackson()
        }
        install(WebSockets)
        loxoneHttp()
        loxoneWebsockets()
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop(1, 1, TimeUnit.SECONDS)
    }

    fun restart() {
        stop()
        server = createServer(port)
        start()
    }

    fun expect(req: Matcher<String>) {
        stubbing.expect(req)
    }

    fun verifyExpectations() = stubbing.messages.all(ResponseStubbing::called)

    private fun Application.loxoneHttp() {
        routing {
            get(DEV_CFG_API.command) {
                call.respond(LoxoneMessage(DEV_CFG_API.command, 200, ApiInfo("50:4F:94:10:B8:4A", "9.1.10.30")))
            }
            get(DEV_SYS_GETPUBLICKEY.command) {
                call.respond(LoxoneMessage(DEV_SYS_GETPUBLICKEY.command, 200, PubKeyInfo(PUBLIC_KEY)))
            }
        }
    }

    private fun Application.loxoneWebsockets() {
        routing {
            webSocket("/ws/rfc6455") {
                incoming.consumeEach { frame ->
                    when(frame) {
                        is Frame.Text -> {
                            processMsg(frame.readText())
                        }
                        else -> println("Non text frame received")
                    }
                }
            }
        }
    }

    private suspend fun WebSocketServerSession.processMsg(msg: String) {
        msgProcessors.firstOrNull() { msgProcessor -> msgProcessor(this, msg) } ?: send("", 400)
    }

    private suspend fun processKeyExchange(session: WebSocketServerSession, msg: String): Boolean {
        return Regex("jdev/sys/keyexchange/(.*)").find(msg)?.let { exchange ->
            val sharedKeyBase64 = exchange.groupValues[1]
            val sharedKeyEnc = sharedKeyBase64.decodeBase64Bytes()
            Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
                init(DECRYPT_MODE, SERVER_PRIVATE_KEY)
                val decryptedKey = String(doFinal(sharedKeyEnc))
                sharedKey = SecretKeySpec(hexToBytes(decryptedKey.substring(0, 64)), "AES")
                sharedKeyIv = hexToBytes(decryptedKey.substring(65))
            }
            session.send("dev/sys/keyexchange/$sharedKeyBase64")
            true
        } ?: false
    }


    private suspend fun processGetKey(session: WebSocketServerSession, msg: String): Boolean {
        return if (msg == "jdev/sys/getkey2/$USER") {
            session.send(USER_KEY)
            true
        } else
            false
    }

    private suspend fun processEncrypted(session: WebSocketServerSession, msg: String): Boolean {
        return Regex("jdev/sys/enc/(.*)").find(msg)?.let { encryptedMsg ->
            @Suppress("BlockingMethodInNonBlockingContext") // TODO fix
            val encrypted = URLDecoder.decode(encryptedMsg.groupValues[1], "UTF-8").decodeBase64Bytes()

            Cipher.getInstance("AES/CBC/ZeroBytePadding").apply {
                init(DECRYPT_MODE, sharedKey, IvParameterSpec(sharedKeyIv))
                val decrypted = Regex("^(.+)/jdev/sys/(.*)\$").find(String(doFinal(encrypted)))

                if (decrypted != null) {
                    session.processMsg(decrypted.groupValues[2])
                }
            }
            true
        } ?: false
    }

    private suspend fun processGetToken(session: WebSocketServerSession, msg: String): Boolean {
        return Regex("gettoken/(?<auth>[^/]+)/(?<user>[^/]+)/(?<tail>[24]/[^/]+/loxoneJava)").find(msg)?.let { gettoken ->
            val auth = gettoken.groups["auth"]!!.value
            val user = gettoken.groups["user"]!!.value
            val tail = gettoken.groups["tail"]!!.value
            val control = "dev/sys/gettoken/$auth/$user/$tail"
            if (0 < badCredentials) {
                badCredentials--
                session.send(control, 401)
            } else {
                if (auth == USER_HASH) {
                    session.send(control, 200, TOKEN)
                } else {
                    session.send(control, 400)
                }
            }
            true
        } ?: false
    }

    private suspend fun processGetUserSalt(session: WebSocketServerSession, msg: String): Boolean {
        return if (msg == "jdev/sys/getvisusalt/$USER") {
            session.send(USER_VISUSALT)
            true
        } else
            false
    }

    private suspend fun processSecured(session: WebSocketServerSession, msg: String): Boolean {
        return Regex("jdev/sps/ios/(?<visuhash>[^/]+)/(?<cmd>.*)").find(msg)?.let { securedMsg ->
            if (securedMsg.groups["visuhash"]!!.value == VISU_HASH) {
                session.processMsg(securedMsg.groups["cmd"]!!.value)
            } else {
                session.send(msg, 401)
            }
            true
        } ?: false
    }

    private suspend fun processEnableUpdates(session: WebSocketServerSession, msg: String): Boolean {
        return if (msg == "jdev/sps/enablebinstatusupdate") {
            session.send("dev/sps/enablebinstatusupdate")
            true
        } else
            false
    }

    private suspend fun processStubbing(session: WebSocketServerSession, msg: String): Boolean {
        stubbing.messages.forEach {
            if (it.request.matches(msg)) {
                session.send(msg, 200, it.response)
                it.called = true
            }
        }
        return true
    }

    private suspend fun WebSocketServerSession.send(control: String, code: Int) {
        val msgVal = when(code) {
            401 -> JsonValue(TextNode("Bad credentials"))
            else -> JsonValue(NullNode.getInstance())

        }
        send(control, code, msgVal)
    }

    private suspend fun WebSocketServerSession.send(control: String, code: Int, value: LoxoneValue) {
        send(LoxoneMessage(control, code, value))
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun WebSocketServerSession.send(loxMsg: LoxoneMessage<*>) {
        send(withContext(Dispatchers.IO) { writeMessage(loxMsg) })
    }
}

internal data class ResponseStubbing(
    val request: Matcher<String>,
    val response: LoxoneValue = JsonValue(TextNode(""))
) {
    var called = false
}

internal class Stubbing {
    val messages = mutableListOf<ResponseStubbing>()

    fun expect(req: Matcher<String>) = ResponseStubbing(req).also { messages.add(it) }
}
