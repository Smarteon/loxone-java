package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AirBaseExtension extends Extension {

    private final List<AirDevice> devices;

    @JsonCreator
    AirBaseExtension(@JsonProperty("Code") final String code, @JsonProperty("Name") final String name,
                     @JsonProperty("Serial") final String serialNumber, @JsonProperty("Version") final String version,
                     @JsonProperty("Online") final Boolean online, @JsonProperty("DummyDev") final Boolean dummy,
                     @JsonProperty("Occupied") final Boolean occupied, @JsonProperty("Interfered") final Boolean interfered,
                     @JsonProperty("IntDev") final Boolean intDev,
                     @JsonProperty("AirDevice") final List<AirDevice> devices) {
        super(code, name, serialNumber, version, online, dummy, occupied, interfered, intDev);
        this.devices = devices;
    }

    public List<AirDevice> getDevices() {
        return devices;
    }
}
