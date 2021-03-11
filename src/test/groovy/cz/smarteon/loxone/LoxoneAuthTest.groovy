package cz.smarteon.loxone

import cz.smarteon.loxone.message.ApiInfo
import cz.smarteon.loxone.message.EncryptedCommand
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneMessageCommand
import cz.smarteon.loxone.message.LoxoneValue
import cz.smarteon.loxone.message.PubKeyInfo
import cz.smarteon.loxone.message.Token
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Timeout
import spock.lang.Unroll

import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static cz.smarteon.loxone.CommandResponseListener.State.CONSUMED
import static cz.smarteon.loxone.CryptoSupport.HASHING
import static cz.smarteon.loxone.CryptoSupport.LOXONE_EPOCH_BEGIN
import static cz.smarteon.loxone.CryptoSupport.PASS
import static cz.smarteon.loxone.CryptoSupport.PUBLIC_KEY
import static cz.smarteon.loxone.CryptoSupport.TOKEN
import static cz.smarteon.loxone.CryptoSupport.USER
import static cz.smarteon.loxone.CryptoSupport.VISU_PASS
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY
import static cz.smarteon.loxone.message.TokenPermissionType.WEB

class LoxoneAuthTest extends Specification {

    @Subject private LoxoneAuth loxoneAuth
    private LoxoneHttp http
    private CommandSender senderMock
    private ScheduledExecutorService scheduler

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())
    }

    void setup() {
        http = Stub(LoxoneHttp) {
            get(DEV_CFG_API) >> new LoxoneMessage<>(DEV_CFG_API.command, 200, new ApiInfo('TestMAC', 'TestVersion'))
            get(DEV_SYS_GETPUBLICKEY) >> new LoxoneMessage<>(DEV_SYS_GETPUBLICKEY.command, 200, new PubKeyInfo(PUBLIC_KEY))
        }
        loxoneAuth = new LoxoneAuth(http, USER, PASS, VISU_PASS)

        senderMock = Mock(CommandSender)
        loxoneAuth.commandSender = senderMock

        scheduler = Executors.newSingleThreadScheduledExecutor()
        loxoneAuth.setAutoRefreshScheduler(scheduler)

        loxoneAuth.init()
    }

    void cleanup() {
        scheduler.shutdownNow()
    }

    @Timeout(5)
    def "test regular flow"() {
        given:
        def keyCmd = LoxoneMessageCommand.getKey(USER)
        def tokenCmd =  EncryptedCommand.getToken(
                LoxoneCrypto.loxoneHashing(PASS, USER, HASHING, "gettoken"),
                USER, WEB, LoxoneAuth.CLIENT_UUID, loxoneAuth.clientInfo, {it}
        )
        def token = new Token(TOKEN.token, TOKEN.key, needsRefreshIn2Secs(), TOKEN.rights, TOKEN.unsecurePassword)
        def refreshLatch = new CountDownLatch(1)

        when:
        loxoneAuth.setAutoRefreshToken(true)
        loxoneAuth.startAuthentication()
        def keyState = loxoneAuth.onCommand(keyCmd, new LoxoneMessage<>(keyCmd.command, 200, HASHING))
        def tokenState = loxoneAuth.onCommand(tokenCmd, new LoxoneMessage<LoxoneValue>(tokenCmd.decryptedCommand, 200, token))

        then:
        keyState == CONSUMED
        tokenState == CONSUMED
        1 * senderMock.send({ it.command ==~ /.*getkey2.*/ })
        1 * senderMock.send({ it.command ==~ /.*keyexchange.*/ })
        1 * senderMock.send({ it.command ==~ /^jdev\/sys\/enc\/.*/ && it.valueType == Token })
        0 * senderMock._

        when:
        def tokenRefreshed = refreshLatch.await(3, TimeUnit.SECONDS) // wait for token refresh
        if (tokenRefreshed) {
            loxoneAuth.onCommand(keyCmd, new LoxoneMessage<>(keyCmd.command, 200, HASHING))
        }

        then:
        tokenRefreshed
        1 * senderMock.send({ it.command ==~ /.*getkey2.*/ }) >> { refreshLatch.countDown() }
        1 * senderMock.send({ it.command ==~ /.*keyexchange.*/ })
        1 * senderMock.send({ it.command ==~ /^jdev\/sys\/enc\/.*/ && it.valueType == Token })
        0 * senderMock._
    }

    @Unroll
    def "should fail #action with visuPass not set"() {
        given:
        def noVisuAuth = new LoxoneAuth(http, USER, PASS, null)

        when:
        noVisuAuth."$function"()

        then:
        thrown(IllegalStateException)

        where:
        action                          | function
        'compute visuHash'              | 'getVisuHash'
        'start visuHash authentication' | 'startVisuAuthentication'
    }

    private static int needsRefreshIn2Secs() {
        return ((System.currentTimeMillis() / 1000) - LOXONE_EPOCH_BEGIN + 302).toInteger()
    }
}
