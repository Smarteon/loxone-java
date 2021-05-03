package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TreeExtension extends Extension {

    private final TreeBranch leftBranch;
    private final TreeBranch rightBranch;

    @JsonCreator
    TreeExtension(@JsonProperty("Code") final String code, @JsonProperty("Name") final String name,
                  @JsonProperty("Serial") final String serialNumber, @JsonProperty("Version") final String version,
                  @JsonProperty("HwVersion") final String hwVersion,
                  @JsonProperty("Online") final Boolean online, @JsonProperty("DummyDev") final Boolean dummy,
                  @JsonProperty("Occupied") final Boolean occupied, @JsonProperty("Interfered") final Boolean interfered,
                  @JsonProperty("IntDev") final Boolean intDev,
                  @JsonProperty("Updating") final Boolean updating,
                  @JsonProperty("ExtUpdateProgress") final Integer updateProgress,
                  @JsonProperty("TreeBranch") final List<TreeBranch> branches) {
        super(code, name, serialNumber, version, hwVersion, online, dummy, occupied, interfered, intDev, updating, updateProgress);
        this.leftBranch = branches.stream().filter(b -> "2".equals(b.getBranch())).findFirst().orElse(null);
        this.rightBranch = branches.stream().filter(b -> "3".equals(b.getBranch())).findFirst().orElse(null);
    }

    /**
     * Left Tree branch if configured.
     * @return left branch or null
     */
    @Nullable
    public TreeBranch getLeftBranch() {
        return leftBranch;
    }

    /**
     * Right Tree branch if configured.
     * @return right branch or null
     */
    @Nullable
    public TreeBranch getRightBranch() {
        return rightBranch;
    }
}
