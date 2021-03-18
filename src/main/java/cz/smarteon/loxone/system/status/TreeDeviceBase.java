package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class TreeDeviceBase extends UpdatableDevice {

    protected final String place;
    protected final String installation;
    protected final String version;
    protected final Boolean online;
    protected final String lastReceived; // TODO time
    protected final Integer timeDiff; // TODO semantics??
    protected final Boolean dummy;

    protected final String hwVersion;
    protected final String mac;
    protected final Boolean occupied;
    protected final Boolean interfered;
    protected final List<AirDevice> airDevices;

    @JsonCreator
    TreeDeviceBase(@JsonProperty("Code") final String code,
                   @JsonProperty("Name") final String name,
                   @JsonProperty("Serial") final String serialNumber,
                   @JsonProperty("Place") final String place,
                   @JsonProperty("Inst") final String installation,
                   @JsonProperty("Version") final String version,
                   @JsonProperty("Online") final Boolean online,
                   @JsonProperty("LastReceived") final String lastReceived,
                   @JsonProperty("TimeDiff") final Integer timeDiff,
                   @JsonProperty("DummyDev") final Boolean dummy,
                   @JsonProperty("Updating") final Boolean updating,
                   @JsonProperty("UpdateProgress") final Integer updateProgress,
                   @JsonProperty("HwVersion") final String hwVersion,
                   @JsonProperty("Mac") final String mac,
                   @JsonProperty("Occupied") final Boolean occupied,
                   @JsonProperty("Interfered") final Boolean interfered,
                   @JsonProperty("AirDevice") final List<AirDevice> airDevices) {
        super(code, name, serialNumber, updating, updateProgress);
        this.place = place;
        this.installation = installation;
        this.version = version;
        this.online = online;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
        this.dummy = dummy;
        this.hwVersion = hwVersion;
        this.mac = mac;
        this.occupied = occupied;
        this.interfered = interfered;
        this.airDevices = airDevices;
    }
}
