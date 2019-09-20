package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Relay Extension", value = RelayExtension.class),
        @JsonSubTypes.Type(name = "Dali Extension", value = DaliExtension.class),
        @JsonSubTypes.Type(name = "Tree Extension", value = TreeExtension.class),
        @JsonSubTypes.Type(name = "Air Base Extension", value = AirBaseExtension.class),
        @JsonSubTypes.Type(name = "RS485 Extension", value = RS485Extension.class),
        @JsonSubTypes.Type(name = "1-Wire Extension", value = OneWireExtension.class)
})
public abstract class Extension {

    protected final String code;
    protected final String name;
    protected final String serialNumber;
    protected final String version;
    protected final Boolean online;
    protected final Boolean dummy;
    protected final Boolean occupied;
    protected final Boolean interfered;
    protected final Boolean intDev;

    protected Extension(final String code, final String name, final String serialNumber, final String version,
                        final Boolean online, final Boolean dummy, final Boolean occupied, final Boolean interfered,
                        final Boolean intDev) {
        this.code = code;
        this.name = name;
        this.serialNumber = serialNumber;
        this.version = version;
        this.online = online;
        this.dummy = dummy;
        this.occupied = occupied;
        this.interfered = interfered;
        this.intDev = intDev;
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

    public String getVersion() {
        return version;
    }

    public Boolean getOnline() {
        return online;
    }

    public Boolean getDummy() {
        return dummy;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public Boolean getInterfered() {
        return interfered;
    }

    public Boolean getIntDev() {
        return intDev;
    }
}
