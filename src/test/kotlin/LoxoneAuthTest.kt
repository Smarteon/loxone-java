package cz.smarteon.loxone

import cz.smarteon.loxone.CommandResponseListener.State.CONSUMED
import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.EncryptedCommand
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneMessageCommand
import cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API
import cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY
import cz.smarteon.loxone.message.PubKeyInfo
import cz.smarteon.loxone.message.Token
import cz.smarteon.loxone.message.TokenPermissionType.WEB
import cz.smarteon.loxone.mock.CryptoMock.HASHING
import cz.smarteon.loxone.mock.CryptoMock.LOXONE_EPOCH_BEGIN
import cz.smarteon.loxone.mock.CryptoMock.PASS
import cz.smarteon.loxone.mock.CryptoMock.PUBLIC_KEY
import cz.smarteon.loxone.mock.CryptoMock.TOKEN
import cz.smarteon.loxone.mock.CryptoMock.USER
import cz.smarteon.loxone.mock.CryptoMock.VISU_PASS
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyAll
import org.awaitility.kotlin.await
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expect
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.security.Security
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicBoolean

@ExtendWith(MockKExtension::class)
class LoxoneAuthTest {

    @MockK
    private lateinit var http: LoxoneHttp

    @RelaxedMockK
    private lateinit var sender: CommandSender

    private lateinit var scheduler: ScheduledExecutorService

    private lateinit var loxoneAuth: LoxoneAuth

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @BeforeEach
    fun setup() {
        every { http.get(DEV_CFG_API) } returns LoxoneMessage(
            DEV_CFG_API.command,
            200,
            ApiInfo("TestMAC", "TestVersion")
        )
        every { http.get(DEV_SYS_GETPUBLICKEY) } returns LoxoneMessage(
            DEV_SYS_GETPUBLICKEY.command,
            200,
            PubKeyInfo(PUBLIC_KEY)
        )

        scheduler = Executors.newSingleThreadScheduledExecutor()

        loxoneAuth = LoxoneAuth(http, USER, PASS, VISU_PASS).apply {
            setCommandSender(sender)
            setAutoRefreshScheduler(scheduler)
            init()
        }
    }

    @Test
    fun `test regular flow`() {
        val keyCmd = LoxoneMessageCommand.getKey(USER)
        val tokenCmd = EncryptedCommand.getToken(
            LoxoneCrypto.loxoneHashing(PASS, USER, HASHING, "gettoken"),
            USER, WEB, LoxoneAuth.CLIENT_UUID, loxoneAuth.clientInfo
        ) { it }
        val token = Token(TOKEN.token, TOKEN.key, needsRefreshIn2Secs(), TOKEN.rights, TOKEN.isUnsecurePassword)

        loxoneAuth.isAutoRefreshToken = true
        loxoneAuth.startAuthentication()

        expect {
            that(loxoneAuth.onCommand(keyCmd, LoxoneMessage(keyCmd.command, 200, HASHING))).isEqualTo(CONSUMED)
            that(loxoneAuth.onCommand(tokenCmd, LoxoneMessage(tokenCmd.decryptedCommand, 200, token))).isEqualTo(CONSUMED)
        }

        verifyAll {
            sender.send(match { Regex(".*getkey2.*").matches(it.command) })
            sender.send(match { Regex(".*keyexchange.*").matches(it.command) })
            sender.send(match { Regex("^jdev/sys/enc.*").matches(it.command) })
        }

        val tokenRefreshed = AtomicBoolean(false)
        every { sender.send(match { Regex(".*getkey2.*").matches(it.command) }) } answers { tokenRefreshed.set(true) }
        await.until { tokenRefreshed.get() }
        expectThat(loxoneAuth.onCommand(keyCmd, LoxoneMessage(keyCmd.command, 200, HASHING))).isEqualTo(CONSUMED)

        verifyAll {
            sender.send(match { Regex(".*getkey2.*").matches(it.command) })
            sender.send(match { Regex(".*keyexchange.*").matches(it.command) })
            sender.send(match { Regex("^jdev/sys/enc.*").matches(it.command) })
        }
    }

    @Test
    fun `should fail without visuPass`() {
        val noVisuAuth = LoxoneAuth(http, USER, PASS, null)
        expectThrows<IllegalStateException> { noVisuAuth.visuHash }
        expectThrows<IllegalStateException> { noVisuAuth.startVisuAuthentication() }
    }

    @AfterEach
    fun cleanup() {
        scheduler.shutdownNow()
    }

    private fun needsRefreshIn2Secs() =
        ((System.currentTimeMillis() / 1000) - LOXONE_EPOCH_BEGIN + 302).toInt()
}
