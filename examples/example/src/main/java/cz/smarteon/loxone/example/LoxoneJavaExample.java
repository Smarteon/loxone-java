package cz.smarteon.loxone.example;

import cz.smarteon.loxone.*;
import cz.smarteon.loxone.app.SwitchControl;
import cz.smarteon.loxone.message.LoxoneMessage;
import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import java.security.Security;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LoxoneJavaExample {

    public static void main(String[] args) {
        final String address = args[0];
        final String user = args[1];
        final String password = args[2];
        final String uiPassword = args[3];

        final CountDownLatch commandLatch = new CountDownLatch(1);
        final CountDownLatch eventLatch = new CountDownLatch(1);

        // Needed under openjdk, alternatively install oracle's JCE.
        Security.addProvider(new BouncyCastleProvider());

        // Initialize loxone with credentials.
        Loxone loxone = new Loxone(new LoxoneEndpoint(address), user, password, uiPassword);

        // Set event listener.
        setEventListener(loxone, eventLatch);

        // Set listener waiting for our command response.
        setCommandResponseListener(loxone, commandLatch);

        // Set Loxone App listener to send a command.
        setLoxoneAppListener(loxone);

        try {
            // Start the service.
            loxone.start();

            // Wait for command and any event.
            if (!commandLatch.await(30, TimeUnit.SECONDS)) {
                System.out.println("Command latch timed out.");
            }
            if (!eventLatch.await(30, TimeUnit.SECONDS)) {
                System.out.println("Event latch timed out.");
            }

        } catch (InterruptedException | LoxoneException e) {
            e.printStackTrace();
        }

        // Release all the resources correctly.
        loxone.stop();
    }

    private static void setEventListener(Loxone loxone, CountDownLatch latch) {
        loxone.setEventsEnabled(true);
        loxone.webSocket().registerListener(new LoxoneEventListener() {
            @Override
            public void onEvent(@NotNull final ValueEvent event) {
                latch.countDown();
                System.out.println("Received value event=" + event);
            }

            @Override
            public void onEvent(@NotNull final TextEvent event) {
                latch.countDown();
                System.out.println("Received text event=" + event);
            }
        });
    }

    private static void setCommandResponseListener(Loxone loxone, CountDownLatch latch) {
        loxone.webSocket().registerListener(new CommandResponseListener<LoxoneMessage>() {
            @Override
            @NotNull
            public State onCommand(@NotNull final Command<? extends LoxoneMessage> command, @NotNull final LoxoneMessage message) {
                System.out.println("Got answer on command=" + command + " message=" + message);
                latch.countDown();
                return State.CONSUMED;
            }

            @Override
            public boolean accepts(@NotNull final Class clazz) {
                return LoxoneMessage.class.equals(clazz);
            }
        });
    }

    private static void setLoxoneAppListener(Loxone loxone) {
        loxone.registerLoxoneAppListener(loxoneApp -> {
            // Obtain any Control and send the command.
            Optional<SwitchControl> switchControl = loxoneApp.getControls(SwitchControl.class).stream().findFirst();
            if (switchControl.isPresent()) {
                loxone.sendControlPulse(switchControl.get());
            } else {
                System.out.println("No switch control was found. Command won't be send.");
            }
        });
    }
}
