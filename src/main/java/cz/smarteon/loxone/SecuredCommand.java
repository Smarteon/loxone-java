package cz.smarteon.loxone;

import cz.smarteon.loxone.message.ControlCommand;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.LoxoneValue;

import static java.util.Objects.requireNonNull;

class SecuredCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String SEC_PREFIX = "jdev/sps/ios";
    private final String visuHash;

    SecuredCommand(final ControlCommand<V> command, final String visuHash) {
        super(command.getControlCommand(), command.getType(), command.getValueType(), command.isHttpSupported(),
                command.isWsSupported(), command.getSupportedMiniservers());
        this.visuHash = requireNonNull(visuHash, "visuHash can't be null");
    }

    /**
     * Secured command with prefix identifying it's secured
     * @return secured command
     */
    @Override
    public String getCommand() {
        return SEC_PREFIX + "/" + visuHash + "/" + super.getCommand();
    }
}
