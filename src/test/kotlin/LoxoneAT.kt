package cz.smarteon.loxone

import cz.smarteon.loxone.app.SwitchControl
import cz.smarteon.loxone.message.JsonValue
import cz.smarteon.loxone.message.LoxoneMessage
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isSuccess
import strikt.assertions.isTrue
import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.LinkedHashMap
import kotlin.collections.first
import kotlin.collections.set

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@EnabledIfEnvironmentVariable(named = LoxoneAT.LOX_ADDRESS, matches = ".*\\S")
@EnabledIfEnvironmentVariable(named = LoxoneAT.LOX_USER, matches = ".*\\S")
@EnabledIfEnvironmentVariable(named = LoxoneAT.LOX_PASS, matches = ".*\\S")
@EnabledIfEnvironmentVariable(named = LoxoneAT.LOX_VISPASS, matches = ".*\\S")
class LoxoneAT {
    private lateinit var loxone: Loxone
    private lateinit var commands: CommandResponseMemory

    private val device by lazy { loxone.app()?.getControl(DEVICE_NAME, SwitchControl::class.java) }
    private val secDevice by lazy { loxone.app()?.getControl(SEC_DEVICE_NAME, SwitchControl::class.java) }

    @BeforeAll
    fun setup() {
        Security.addProvider((BouncyCastleProvider()))

        val port = if (System.getenv(LOX_PORT) != null) System.getenv(LOX_PORT).toInt() else 80
        val useSsl = if (System.getenv(LOX_SSL) != null) System.getenv(LOX_SSL).toBoolean() else false
        val endpoint = LoxoneEndpoint(System.getenv(LOX_ADDRESS), port, useSsl)
        loxone = Loxone(endpoint, System.getenv(LOX_USER), System.getenv(LOX_PASS), System.getenv(LOX_VISPASS))

        commands = CommandResponseMemory()
        loxone.webSocket().registerListener(commands)

        loxone.start()
    }

    @AfterEach
    fun cleanupEach() {
        commands.clear()
    }

    @AfterAll
    fun cleanupAll() {
        loxone.stop()
    }

    @Test
    @Order(1)
    fun `should test app control exist`() {
        expectThat(loxone.app()).isNotNull()
    }

    @Test
    @Order(2)
    fun `should have testing devices`() {
        expectThat(device).isNotNull()
        expectThat(secDevice).isNotNull().get { isSecured }.isTrue()
    }

    @Test
    @Order(3)
    fun `should pulse on switch`() {
        val latch = commands.expectCommand(".*${device?.uuid}/Pulse")
        device?.let {device -> loxone.sendControlPulse(device) }

        expectCatching {
            withContext(Dispatchers.IO) {
                latch.await(1, TimeUnit.SECONDS)
            } }.isSuccess()
        expectThat(commands){
            get { matched }.hasSize(1)
            get { matched.values.first() }.isA<LoxoneMessage<*>>()
                .get { value }.isA<JsonValue>()
                .get { jsonNode.textValue() }.isEqualTo("1")
        }
    }

    @Test
    @Order(4)
    fun `should pulse on secured switch`() {
        val latch = commands.expectCommand(".*${secDevice?.uuid}/Pulse")
        secDevice?.let {secDevice -> loxone.sendControlPulse(secDevice) }

        expectCatching {
            withContext(Dispatchers.IO) {
                latch.await(1, TimeUnit.SECONDS)
            } }.isSuccess()

        expectThat(commands){
            get { matched }.hasSize(1)
            get { matched.values.first() }.isA<LoxoneMessage<*>>()
                .get { value }.isA<JsonValue>()
                .get { jsonNode.textValue() }.isEqualTo("1")
        }
    }

    @Test
    @Order(5)
    fun `should refresh token`() {
        val evaluator = mockk<TokenStateEvaluator> {
            every { evaluate(any()) } answers { mockk {
                every { needsRefresh() } returns true
            } }
        }
        loxone.auth().setTokenStateEvaluator(evaluator)

        val latch = CountDownLatch(1)
        loxone.auth().startAuthentication()
        loxone.auth().registerAuthListener(mockk {
            every { authCompleted() } answers { latch.countDown() }
        })

        latch.await(1, TimeUnit.SECONDS)
    }

    class CommandResponseMemory: CommandResponseListener<Any>{
        private val all = LinkedHashMap<Any, Any>()
        val matched = LinkedHashMap<Any, Any>()
        private lateinit var pattern: String
        private lateinit var latch: CountDownLatch

        override fun onCommand(command: Command<out Any>, value: Any): CommandResponseListener.State {
            if (this::pattern.isInitialized && this::latch.isInitialized){
                if (command.command.matches(pattern.toRegex()) ) {
                    matched[command] = value
                    latch.countDown()
                }
            }

            all[command] = value
            return CommandResponseListener.State.CONSUMED
        }

        override fun accepts(clazz: Class<*>): Boolean = true

        fun expectCommand(pattern: String): CountDownLatch {
            this.pattern = pattern
            this.latch = CountDownLatch(1)
            return this.latch
        }

        fun clear() {
            all.clear()
            matched.clear()
        }
    }

    companion object{
        const val LOX_ADDRESS = "LOX_ADDRESS"
        const val LOX_USER = "LOX_USER"
        const val LOX_PASS = "LOX_PASS"
        const val LOX_VISPASS = "LOX_VISPASS"
        private const val LOX_PORT = "LOX_PORT"
        private const val LOX_SSL = "LOX_SSL"

        private const val DEVICE_NAME = "SmarteonTest-Switch"
        private const val SEC_DEVICE_NAME = "SmarteonTest-SwitchSecured"
    }
}
