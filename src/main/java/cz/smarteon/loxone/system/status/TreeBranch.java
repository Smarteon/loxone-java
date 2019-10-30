package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class TreeBranch implements DevicesProvider<TreeDevice> {

    private final String branch;
    private final Integer devicesCount;
    private final Integer errors;
    private final List<TreeDevice> devices;

    @JsonCreator
    TreeBranch(@JsonProperty("Branch") final String branch,
               @JsonProperty("Devices") final Integer devicesCount,
               @JsonProperty("Errors") final Integer errors,
               @JsonProperty("TreeDevice") final List<TreeDevice> devices) {
        this.branch = branch;
        this.devicesCount = devicesCount;
        this.errors = errors;
        this.devices = devices;
    }

    public String getBranch() {
        return branch;
    }

    public Integer getDevicesCount() {
        return devicesCount;
    }

    public Integer getErrors() {
        return errors;
    }

    public List<TreeDevice> getDevices() {
        return devices != null ? devices : Collections.emptyList();
    }
}
