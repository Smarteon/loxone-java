package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a tree to air extension.
 */
public class TreeToAirBridge extends TreeDevice implements DevicesProvider<AirDevice> {

    TreeToAirBridge(final TreeDeviceBase treeDeviceBase) {
        super(treeDeviceBase);
    }

    @Nullable
    public String getHwVersion() {
        return hwVersion;
    }

    @Nullable
    public String getMac() {
        return mac;
    }

    @Nullable
    public Boolean getOccupied() {
        return occupied;
    }

    @Nullable
    public Boolean getInterfered() {
        return interfered;
    }

    @Override
    @NotNull
    public List<AirDevice> getDevices() {
        return airDevices != null ? airDevices : Collections.emptyList();
    }
}
