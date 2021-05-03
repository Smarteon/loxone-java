package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Miniserver extension which is not recognized by this library.
 */
public class UnrecognizedExtension extends Extension {

    @JsonCreator
    UnrecognizedExtension(@JsonProperty("Code") final String code, @JsonProperty("Name") final String name,
                          @JsonProperty("Serial") final String serialNumber, @JsonProperty("Version") final String version,
                          @JsonProperty("HwVersion") final String hwVersion,
                          @JsonProperty("Online") final Boolean online, @JsonProperty("DummyDev") final Boolean dummy,
                          @JsonProperty("Occupied") final Boolean occupied, @JsonProperty("Interfered") final Boolean interfered,
                          @JsonProperty("IntDev") final Boolean intDev,
                          @JsonProperty("Updating") final Boolean updating,
                          @JsonProperty("ExtUpdateProgress") final Integer updateProgress) {
        super(code, name, serialNumber, version, hwVersion, online, dummy, occupied, interfered, intDev, updating, updateProgress);
    }
}
