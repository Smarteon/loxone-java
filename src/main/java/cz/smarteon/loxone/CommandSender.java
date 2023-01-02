package cz.smarteon.loxone;

interface CommandSender {

    void send(Command<?> command);
}
