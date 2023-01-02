package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Common predecessor to all miniserver's extensions.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Extension implements Updatable {

    @XmlAttribute(name = "Type") protected String type;
    @XmlAttribute(name = "Code") protected String code;
    @XmlAttribute(name = "Name") protected String name;
    @XmlAttribute(name = "Serial") protected String serialNumber;
    @XmlAttribute(name = "Version") protected String version;
    @XmlAttribute(name = "HwVersion") protected String hwVersion;
    @XmlAttribute(name = "Online") protected Boolean online;
    @XmlAttribute(name = "DummyDev") protected Boolean dummy;
    @XmlAttribute(name = "Occupied") protected Boolean occupied;
    @XmlAttribute(name = "Interfered") protected Boolean interfered;
    @XmlAttribute(name = "IntDev") protected Boolean intDev;
    @XmlAttribute(name = "Updating") protected Boolean updating;
    @XmlAttribute(name = "ExtUpdateProgress") protected Integer updateProgress;

    @XmlElement(name = "AirDevice") @XmlJavaTypeAdapter(AirDeviceAdapter.class) protected List<AirDevice> airDevices;
    @XmlElement(name = "TreeBranch") protected List<TreeBranch> treeBranches;
    @XmlElement(name = "DaliDevice") protected List<DaliDevice> daliDevices;
    @XmlElement(name = "OneWireDevice") protected List<OneWireDevice> oneWireDevices;

    Extension() { }

    @SuppressWarnings("checkstyle:parameternumber")
    Extension(final String type, final String code, final String name, final String serialNumber, final String version,
              final String hwVersion, final Boolean online, final Boolean dummy, final Boolean occupied,
              final Boolean interfered, final Boolean intDev, final Boolean updating, final Integer updateProgress) {
        this.type = type;
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
     * Extension type as represent in status payload.
     * @return type
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * Extension code.
     * @return code
     */
    @Nullable
    public String getCode() {
        return code;
    }

    /**
     * Extension configured name.
     * @return name
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Extension serial number (set by factory).
     * @return serial number
     */
    @Nullable
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Extension version.
     * @return version
     */
    @Nullable
    public String getVersion() {
        return version;
    }

    /**
     * Extension hwVersion.
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
     * Internal device.
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
