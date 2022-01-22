package cz.smarteon.loxone.system.status;

public class DaliDevice extends Device {

    private Boolean error;

    DaliDevice() {}

    public boolean isError() {
        return Boolean.FALSE.equals(error);
    }
}
