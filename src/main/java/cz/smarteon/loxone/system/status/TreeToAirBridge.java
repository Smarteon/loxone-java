package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TreeToAirBridge extends TreeDevice implements DevicesProvider<AirDevice> {

    private final String hwVersion;
    private final String mac;
    private final Boolean occupied;
    private final Boolean interfered;
    private final List<AirDevice> devices;

    @JsonCreator
    TreeToAirBridge(@JsonProperty("Code") final String code,
                    @JsonProperty("Name") final String name,
                    @JsonProperty("Serial") final String serialNumber,
                    @JsonProperty("Place") final String place,
                    @JsonProperty("Inst") final String installation,
                    @JsonProperty("Version") final String version,
                    @JsonProperty("Online") final Boolean online,
                    @JsonProperty("LastReceived") final String lastReceived,
                    @JsonProperty("TimeDiff") final Integer timeDiff,
                    @JsonProperty("DummyDev") final Boolean dummy,
                    @JsonProperty("HwVersion") final String hwVersion,
                    @JsonProperty("Mac") final String mac,
                    @JsonProperty("Occupied") final Boolean occupied,
                    @JsonProperty("Interfered") final Boolean interfered,
                    @JsonProperty("AirDevice") final List<AirDevice> devices) {
        super(code, name, serialNumber, place, installation, version, online, lastReceived, timeDiff, dummy);
        this.hwVersion = hwVersion;
        this.mac = mac;
        this.occupied = occupied;
        this.interfered = interfered;
        this.devices = devices;
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
        return devices != null ? devices : Collections.emptyList();
    }
}
