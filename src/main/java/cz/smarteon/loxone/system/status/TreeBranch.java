package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
