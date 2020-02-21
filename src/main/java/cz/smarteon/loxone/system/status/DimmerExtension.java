package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DimmerExtension extends Extension {

    @JsonCreator
    DimmerExtension(@JsonProperty("Code") final String code, @JsonProperty("Name") final String name,
                    @JsonProperty("Serial") final String serialNumber, @JsonProperty("Version") final String version,
                    @JsonProperty("Online") final Boolean online, @JsonProperty("DummyDev") final Boolean dummy,
                    @JsonProperty("Occupied") final Boolean occupied, @JsonProperty("Interfered") final Boolean interfered,
                    @JsonProperty("IntDev") final Boolean intDev) {
        super(code, name, serialNumber, version, online, dummy, occupied, interfered, intDev);
    }
}
