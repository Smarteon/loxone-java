package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a tree device.
 */
public class TreeDevice extends TreeDeviceBase {

    TreeDevice(final TreeDeviceBase td) {
        super(td.getCode(), td.getName(), td.getSerialNumber(), td.place, td.installation, td.version, td.online,
                td.lastReceived, td.timeDiff, td.dummy, td.updating, td.updateProgress, td.hwVersion, td.mac,
                td.occupied, td.interfered, td.airDevices);
    }

    @Nullable
    public String getPlace() {
        return place;
    }

    @Nullable
    public String getInstallation() {
        return installation;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    @Nullable
    public String getLastReceived() {
        return lastReceived;
    }

    @Nullable
    public Integer getTimeDiff() {
        return timeDiff;
    }

    public boolean isDummy() {
        return Boolean.TRUE.equals(dummy);
    }
}
