package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DaliExtension extends Extension implements DevicesProvider<DaliDevice> {

    private final List<DaliDevice> devices;

    DaliExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied, e.interfered,
                e.intDev, e.updating, e.updateProgress);
        devices = e.daliDevices;
    }

    @NotNull
    public List<DaliDevice> getDevices() {
        return devices != null ? devices : Collections.emptyList();
    }
}
