package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for devices.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Device {

    @XmlAttribute(name = "Code") private String code;
    @XmlAttribute(name = "Name") private String name;
    @XmlAttribute(name = "Serial") private String serialNumber;

    Device() { }

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
