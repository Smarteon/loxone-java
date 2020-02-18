package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;

/**
 * Common predecessor to all miniserver's extensions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Relay Extension", value = RelayExtension.class),
        @JsonSubTypes.Type(name = "Dali Extension", value = DaliExtension.class),
        @JsonSubTypes.Type(name = "Tree Extension", value = TreeExtension.class),
        @JsonSubTypes.Type(name = "Air Base Extension", value = AirBaseExtension.class),
        @JsonSubTypes.Type(name = "RS485 Extension", value = RS485Extension.class),
        @JsonSubTypes.Type(name = "1-Wire Extension", value = OneWireExtension.class),
        @JsonSubTypes.Type(name = "DMX Extension", value = DmxExtension.class),
        @JsonSubTypes.Type(name = "DI Extension", value = DiExtension.class)
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

    /**
     * Extension code
     * @return code
     */
    @Nullable
    public String getCode() {
        return code;
    }

    /**
     * Extension configured name
     * @return name
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Extension serial number (set by factory)
     * @return serial number
     */
    @Nullable
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Extension version
     * @return version
     */
    @Nullable
    public String getVersion() {
        return version;
    }

    /**
     * Whether this extension is online on loxone - link.
     * @return true when online, false otherwise
     */
    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    /**
     * Whether this extension is configured as dummy.
     * @return true when dummy, false otherwise
     */
    public boolean isDummy() {
        return Boolean.TRUE.equals(dummy);
    }

    /**
     * Whether this extension is occupied.
     * @return false when not occupied, true otherwise
     */
    public boolean isOccupied() {
        return Boolean.FALSE.equals(occupied);
    }

    /**
     * Whether this extension is interfered.
     * @return false when not interfered, true otherwise
     */
    public boolean isInterfered() {
        return Boolean.FALSE.equals(interfered);
    }

    /**
     * Internal device
     * @return true if internal, false otherwise
     */
    public boolean isIntDev() {
        return Boolean.TRUE.equals(intDev);
    }
}
