package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.Nullable;

public class TreeExtension extends Extension {

    private final TreeBranch leftBranch;
    private final TreeBranch rightBranch;

    TreeExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied, e.interfered,
                e.intDev, e.updating, e.updateProgress);
        leftBranch = e.treeBranches.stream().filter(b -> "2".equals(b.getBranch())).findFirst().orElse(null);
        rightBranch = e.treeBranches.stream().filter(b -> "3".equals(b.getBranch())).findFirst().orElse(null);
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
