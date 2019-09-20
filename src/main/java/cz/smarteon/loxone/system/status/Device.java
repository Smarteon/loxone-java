package cz.smarteon.loxone.system.status;

public abstract class Device {

    private final String code;
    private final String name;
    private final String serialNumber;

    protected Device(final String code, final String name, final String serialNumber) {
        this.code = code;
        this.name = name;
        this.serialNumber = serialNumber;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
