package cz.smarteon.loxone

import cz.smarteon.loxone.message.LoxoneValue
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static java.lang.System.getenv

@Requires({(
        env[LOX_ADDRESS]
        && env[LOX_USER]
        && env[LOX_PASS]
        && env[LOX_VISPASS]
        && env[LOX_ALARM]
    )})
class LoxoneWebSocketAT extends Specification {

    public static final String LOX_ADDRESS = 'LOX_ADDRESS'
    public static final String LOX_USER = 'LOX_USER'
    public static final String LOX_PASS = 'LOX_PASS'
    public static final String LOX_VISPASS = 'LOX_VISPASS'
    public static final String LOX_ALARM = 'LOX_ALARM'

    @Shared LoxoneWebSocket loxoneWebSocket
    @Shared CommandMemory commands
    @Shared def alarmId

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())

        def address = getenv(LOX_ADDRESS)
        final LoxoneAuth loxoneAuth = new LoxoneAuth(new LoxoneHttp(address), getenv(LOX_USER), getenv(LOX_PASS), getenv(LOX_VISPASS))
        loxoneWebSocket = new LoxoneWebSocket(address, loxoneAuth)

        commands = new CommandMemory()
        loxoneWebSocket.registerListener(commands)

        alarmId = getenv(LOX_ALARM)
    }

    void cleanup() {
        loxoneWebSocket.close()
    }

    def "should ask for alarm status"() {
        given:
        def latch = commands.expectCommand(".*/$alarmId/all")

        when:
        loxoneWebSocket.sendSecureCommand("$alarmId/all")

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.commands.size() == 3
    }

    private static class CommandMemory implements CommandListener {

        LinkedHashMap commands = new LinkedHashMap()
        def pattern
        CountDownLatch latch

        @Override
        CommandListener.State onCommand(String command, LoxoneValue value) {
            if (latch != null && pattern != null)  {
                if (command ==~ pattern) {
                    latch.countDown()
                }
            }
            commands.put(command, value)
            return CommandListener.State.CONSUMED
        }

        CountDownLatch expectCommand(def pattern) {
            this.pattern = pattern
            this.latch = new CountDownLatch(1)
            return this.latch
        }
    }
}
