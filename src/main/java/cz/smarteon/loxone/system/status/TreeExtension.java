package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a tree extension.
 */
public class TreeExtension extends Extension {

    private TreeBranch leftBranch;
    private TreeBranch rightBranch;

    TreeExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied,
                e.interfered, e.intDev, e.updating, e.updateProgress);
        setBranches(e.treeBranches);
    }

    TreeExtension(final TreeBranch branch) {
        super("BuiltIn Tree", null, "BuiltIn Tree", branch.serialNumber, branch.version, null, true, false,
                null, null, null, null, null);
        setBranches(Collections.singletonList(branch));
    }

    private void setBranches(final List<TreeBranch> branches) {
        leftBranch = branches.stream().filter(b -> "2".equals(b.getBranch())).findFirst().orElse(null);
        rightBranch = branches.stream().filter(b -> "3".equals(b.getBranch())).findFirst().orElse(null);
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
