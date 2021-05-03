package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;

/**
 * Common predecessor to all miniserver's extensions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type", defaultImpl = UnrecognizedExtension.class)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Relay Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Dali Extension", value = DaliExtension.class),
        @JsonSubTypes.Type(name = "Tree Extension", value = TreeExtension.class),
        @JsonSubTypes.Type(name = "Air Base Extension", value = AirBaseExtension.class),
        @JsonSubTypes.Type(name = "RS485 Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "1-Wire Extension", value = OneWireExtension.class),
        @JsonSubTypes.Type(name = "DMX Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "DI Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Modbus Extension", value = BasicExtension.class),
        @JsonSubTypes.Type(name = "Dimmer Extension", value = BasicExtension.class)
})
public abstract class Extension implements Updatable {

    protected final String code;
    protected final String name;
    protected final String serialNumber;
    protected final String version;
    protected final String hwVersion;
    protected final Boolean online;
    protected final Boolean dummy;
    protected final Boolean occupied;
    protected final Boolean interfered;
    protected final Boolean intDev;
    protected final Boolean updating;
    protected final Integer updateProgress;

    protected Extension(final String code, final String name, final String serialNumber, final String version, final String hwVersion,
                        final Boolean online, final Boolean dummy, final Boolean occupied, final Boolean interfered,
                        final Boolean intDev, final Boolean updating, final Integer updateProgress) {
        this.code = code;
        this.name = name;
        this.serialNumber = serialNumber;
        this.version = version;
        this.hwVersion = hwVersion;
        this.online = online;
        this.dummy = dummy;
        this.occupied = occupied;
        this.interfered = interfered;
        this.intDev = intDev;
        this.updating = updating;
        this.updateProgress = updateProgress;
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
     * Extension hwVersion
     * @return hwVersion
     */
    @Nullable
    public String getHwVersion() {
        return hwVersion;
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

    @Override
    public boolean isUpdating() {
        return Boolean.TRUE.equals(updating);
    }

    @JsonProperty("ExtUpdateProgress")
    @Override
    public Integer getUpdateProgress() {
        return updateProgress;
    }
}
