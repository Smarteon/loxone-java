package cz.smarteon.loxone.example;

import cz.smarteon.loxone.*;
import cz.smarteon.loxone.config.LoxoneConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.CountDownLatch;

public class LoxoneJavaExample {

    public static void main(String[] args)  {
        final String address = args[0];
        final String user = args[1];
        final String password = args[2];
        final String uiPassword = args[3];

        // needed under openjdk, alternatively install oracle's JCE
        Security.addProvider(new BouncyCastleProvider());

        // create the HTTP and websocket
        LoxoneHttp loxoneHttp = new LoxoneHttp(address);
        LoxoneWebSocket loxoneWebSocket = new LoxoneWebSocket(address, new LoxoneAuth(loxoneHttp, user, password, uiPassword));


        // get LoxAPP3.json using http (websocket not yet supported)
        LoxoneConfig loxoneConfig = loxoneHttp.get(Protocol.C_APP, loxoneWebSocket.getLoxoneAuth(), LoxoneConfig.class);

        // get all the info for first control in config using websocket
        loxoneConfig.getControls().entrySet().stream().findFirst().ifPresent(e -> {

            final String controlId = e.getKey().toString();
            final CountDownLatch latch = new CountDownLatch(1);

            // set listener waiting for our command
            loxoneWebSocket.registerListener((command, value) -> {
                System.out.println("Got answer on command=" + command + " value=" + value);
                if (command.contains(controlId)) {
                    latch.countDown();
                }
                return CommandListener.State.CONSUMED;
            });

            // send the command
            loxoneWebSocket.sendCommand(Protocol.jsonControlAll(controlId));

            try {
                latch.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace(); //ugly :(
            }
        });


        loxoneWebSocket.sendCommand(Protocol.C_APP);

        loxoneWebSocket.close();  // websocket lib creates daemon threads so we need to close explicitly
    }
}
