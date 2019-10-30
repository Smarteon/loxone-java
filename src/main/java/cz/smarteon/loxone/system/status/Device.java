package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.Nullable;

public abstract class Device {

    private final String code;
    private final String name;
    private final String serialNumber;

    protected Device(final String code, final String name, final String serialNumber) {
        this.code = code;
        this.name = name;
        this.serialNumber = serialNumber;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getSerialNumber() {
        return serialNumber;
    }
}
