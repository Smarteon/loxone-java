package cz.smarteon.loxone

import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class LoxoneWebSocketAT extends Specification {

    private static final String ADDRESS = '192.168.88.246'
    private static final String USER = 'showroom'
    private static final String PASS = 'showroom'
    private static final String VIS_PASS = '1234'
    private static final String ALARM_ID = '0f86a2fe-0378-3e15-ffff373f9870b52a'

    @Shared LoxoneWebSocket loxoneWebSocket
    @Shared CommandMemory commands

    void setupSpec() {
        final LoxoneAuth loxoneAuth = new LoxoneAuth(new LoxoneHttp(ADDRESS), USER, PASS, VIS_PASS)
        loxoneWebSocket = new LoxoneWebSocket(ADDRESS, loxoneAuth)

        commands = new CommandMemory()
        loxoneWebSocket.registerListener(commands)
    }

    void cleanup() {
        loxoneWebSocket.close()
    }

    def "should ask for alarm status"() {
        given:
        def latch = commands.expectCommand(".*/$ALARM_ID/all")

        when:
        loxoneWebSocket.sendSecureCommand("$ALARM_ID/all")

        then:
        latch.await(1, TimeUnit.SECONDS)
        commands.commands.size() == 3
    }

    private static class CommandMemory implements CommandListener {

        LinkedHashMap commands = new LinkedHashMap()
        def pattern
        CountDownLatch latch

        @Override
        CommandListener.State onCommand(String command, Object value) {
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
