package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a miniserver's tree branch.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeBranch implements DevicesProvider<TreeDevice> {

    @XmlAttribute(name = "Branch") private String branch;
    @XmlAttribute(name = "Devices") private Integer devicesCount;
    @XmlAttribute(name = "Errors") private Integer errors;
    @XmlElement(name = "TreeDevice") @XmlJavaTypeAdapter(TreeDeviceAdapter.class) private List<TreeDevice> devices;

    // only for builtin tree
    @XmlAttribute(name = "Serial") String serialNumber;
    @XmlAttribute(name = "Version") String version;

    TreeBranch() { }

    @Nullable
    public String getBranch() {
        return branch;
    }

    @Nullable
    public Integer getDevicesCount() {
        return devicesCount;
    }

    @Nullable
    public Integer getErrors() {
        return errors;
    }

    @NotNull
    public List<TreeDevice> getDevices() {
        return devices != null ? devices : Collections.emptyList();
    }
}
