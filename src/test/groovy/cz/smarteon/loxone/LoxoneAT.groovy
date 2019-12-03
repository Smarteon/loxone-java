package cz.smarteon.loxone

import cz.smarteon.loxone.app.SwitchControl
import cz.smarteon.loxone.message.JsonValue
import cz.smarteon.loxone.message.LoxoneMessage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

import java.security.Security
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static java.lang.System.getenv

@Requires({(
        env[LOX_ADDRESS]
        && env[LOX_USER]
        && env[LOX_PASS]
        && env[LOX_VISPASS]
    )})
@Stepwise
class LoxoneAT extends Specification {

    public static final String LOX_ADDRESS = 'LOX_ADDRESS'
    public static final String LOX_PORT = 'LOX_PORT'
    public static final String LOX_SSL = 'LOX_SSL'
    public static final String LOX_USER = 'LOX_USER'
    public static final String LOX_PASS = 'LOX_PASS'
    public static final String LOX_VISPASS = 'LOX_VISPASS'

    // These devices of type Switch should be present in tested config
    private static final String DEVICE_NAME = 'SmarteonTest-Switch'
    private static final String SEC_DEVICE_NAME = 'SmarteonTest-SwitchSecured'

    @Shared @Subject Loxone loxone
    @Shared CommandResponseMemory commands

    @Shared SwitchControl device
    @Shared SwitchControl secDevice

    void setupSpec() {
        Security.addProvider(new BouncyCastleProvider())

        def port = getenv(LOX_PORT) != null ? getenv(LOX_PORT) as Integer : 80
        def useSsl = getenv(LOX_SSL) != null ? Boolean.valueOf(getenv(LOX_SSL)) : false
        def endpoint = new LoxoneEndpoint(getenv(LOX_ADDRESS), port, useSsl)
        loxone = new Loxone(endpoint,getenv(LOX_USER), getenv(LOX_PASS), getenv(LOX_VISPASS))

        commands = new CommandResponseMemory()
        loxone.webSocket().registerListener(commands)

        loxone.start()
    }

    void cleanup() {
        commands.clear()
    }

    void cleanupSpec() {
        loxone.stop()
    }

    def "should be started and testing control exist"() {
        expect:
        loxone.http() != null
        loxone.webSocket() != null
        loxone.auth() != null
        loxone.app() != null
    }

    def "should have testing devices"() {
        when:
        device = loxone.app().getControl(DEVICE_NAME, SwitchControl)
        secDevice = loxone.app().getControl(SEC_DEVICE_NAME, SwitchControl)

        then:
        device != null
        secDevice != null
        secDevice.isSecured()
    }

    def "should pulse on switch"() {
        when:
        def latch = commands.expectCommand(".*$device.uuid/Pulse")

        loxone.sendControlPulse(device)

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.matched.size() == 1
        commands.matched.values()[0] instanceof LoxoneMessage
        ((commands.matched.values()[0] as LoxoneMessage).value as JsonValue).jsonNode.textValue() == '1'
    }

    def "should pulse on secured switch"() {
        when:
        def latch = commands.expectCommand(".*$secDevice.uuid/Pulse")

        loxone.sendControlPulse(secDevice)

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.matched.size() == 1
        commands.matched.values()[0] instanceof LoxoneMessage
        ((commands.matched.values()[0] as LoxoneMessage).value as JsonValue).jsonNode.textValue() == '1'
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
