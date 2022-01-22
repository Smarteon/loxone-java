package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AirBaseExtension extends Extension implements DevicesProvider<AirDevice> {

    private final List<AirDevice> devices;

    AirBaseExtension(final Extension e) {
        super(e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied, e.interfered,
                e.intDev, e.updating, e.updateProgress);
        devices = e.airDevices;
    }

    @NotNull
    public List<AirDevice> getDevices() {
        return devices != null ? devices : Collections.emptyList();
    }
}
