package cz.smarteon.loxone.system.status;

/**
 * Represents a dali device.
 */
public class DaliDevice extends Device {

    private Boolean error;

    DaliDevice() { }

    public boolean isError() {
        return Boolean.FALSE.equals(error);
    }
}
