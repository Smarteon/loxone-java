package cz.smarteon.loxone;

interface CommandSender {

    void send(final Command<?> command);
}
