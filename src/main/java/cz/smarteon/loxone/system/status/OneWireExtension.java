package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents a one wire extension.
 */
public class OneWireExtension extends Extension implements DevicesProvider<OneWireDevice> {

    private final List<OneWireDevice> devices;

    OneWireExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied,
                e.interfered, e.intDev, e.updating, e.updateProgress);
        devices = e.oneWireDevices;
    }

    @NotNull
    public List<OneWireDevice> getDevices() {
        return devices != null ? devices : Collections.emptyList();
    }
}
