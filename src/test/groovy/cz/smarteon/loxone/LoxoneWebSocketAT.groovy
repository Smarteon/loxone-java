package cz.smarteon.loxone


import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static cz.smarteon.loxone.message.ControlCommand.genericControlCommand
import static java.lang.System.getenv

@Requires({(
        env[LOX_ADDRESS]
        && env[LOX_USER]
        && env[LOX_PASS]
        && env[LOX_VISPASS]
        && env[LOX_DEVICE]
    )})
class LoxoneWebSocketAT extends Specification {

    public static final String LOX_ADDRESS = 'LOX_ADDRESS'
    public static final String LOX_USER = 'LOX_USER'
    public static final String LOX_PASS = 'LOX_PASS'
    public static final String LOX_VISPASS = 'LOX_VISPASS'
    public static final String LOX_DEVICE = 'LOX_DEVICE'

    @Shared LoxoneWebSocket loxoneWebSocket
    @Shared String deviceId
    @Shared CommandResponseMemory commands

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())

        def endpoint = new LoxoneEndpoint(getenv(LOX_ADDRESS))
        final LoxoneAuth loxoneAuth = new LoxoneAuth(new LoxoneHttp(endpoint), getenv(LOX_USER), getenv(LOX_PASS), getenv(LOX_VISPASS))
        loxoneWebSocket = new LoxoneWebSocket(endpoint, loxoneAuth)

        commands = new CommandResponseMemory()
        loxoneWebSocket.registerListener(commands)

        deviceId = getenv(LOX_DEVICE)
    }

    void cleanup() {
        commands.clear()
    }

    void cleanupSpec() {
        loxoneWebSocket.close()
    }

    /**
     * This only passes when the deviceId is not guarded by visualization password
     */
    def "should ask for device status"() {
        given:
        def latch = commands.expectCommand(".*/$deviceId/all")

        when:
        loxoneWebSocket.sendCommand(genericControlCommand(deviceId, 'all'))

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.matched.size() == 1
    }

    /**
     * This only passes when the deviceId is really guarded by visualization password
     */
    def "should ask for secured device status"() {
        given:
        def latch = commands.expectCommand(".*/$deviceId/all")

        when:
        loxoneWebSocket.sendSecureCommand(genericControlCommand(deviceId, 'all'))

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.matched.size() == 1
    }

    private static class CommandResponseMemory implements CommandResponseListener {

        LinkedHashMap all = new LinkedHashMap()
        LinkedHashMap matched = new LinkedHashMap()
        def pattern
        CountDownLatch latch

        @Override
        State onCommand(Command command, Object value) {
            if (latch != null && pattern != null)  {
                if (command.command ==~ pattern) {
                    matched.put(command, value)
                    latch.countDown()
                }
            }
            all.put(command, value)
            return State.CONSUMED
        }

        @Override
        boolean accepts(final Class clazz) {
            return true
        }

        CountDownLatch expectCommand(def pattern) {
            this.pattern = pattern
            this.latch = new CountDownLatch(1)
            return this.latch
        }

        void clear() {
            all.clear()
            matched.clear()
        }
    }
}
