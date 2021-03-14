package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class AirDeviceBase extends UpdatableDevice {

    protected final String type;
    protected final String place;
    protected final String installation;
    protected final String lastReceived; // TODO time
    protected final Integer timeDiff; // TODO semantics??
    protected final String version;
    protected final String minVersion; // TODO semantics??
    protected final String hwVersion;
    protected final Integer hops;
    protected final Integer roundTripTime;
    protected final String qualityExt; // TODO semantics??
    protected final String qualityDev; // TODO semantics??
    protected final Boolean online;
    protected final Integer battery;
    protected final Boolean dummy;
    protected final List<OneWireDevice> oneWireDevices;

    @JsonCreator
    AirDeviceBase(@JsonProperty("Type") final String type,
              @JsonProperty("Code") final String code,
              @JsonProperty("Name") final String name,
              @JsonProperty("Place") final String place,
              @JsonProperty("Inst") final String installation,
              @JsonProperty("Serial") final String serialNumber,
              @JsonProperty("LastReceived") final String lastReceived,
              @JsonProperty("TimeDiff") final Integer timeDiff,
              @JsonProperty("Version") final String version,
              @JsonProperty("MinVersion") final String minVersion,
              @JsonProperty("HwVersion") final String hwVersion,
              @JsonProperty("Hops") final Integer hops,
              @JsonProperty("RoundTripTime") final Integer roundTripTime,
              @JsonProperty("QualityExt") final String qualityExt,
              @JsonProperty("QualityDev") final String qualityDev,
              @JsonProperty("Online") final Boolean online,
              @JsonProperty("Battery") final Integer battery,
              @JsonProperty("DummyDev") final Boolean dummy,
              @JsonProperty("Updating") final Boolean updating,
              @JsonProperty("UpdateProgress") final Integer updateProgress,
              @JsonProperty("OneWireDevice") final List<OneWireDevice> oneWireDevices) {
        super(code, name, serialNumber, updating, updateProgress);
        this.type = type;
        this.place = place;
        this.installation = installation;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
        this.version = version;
        this.minVersion = minVersion;
        this.hwVersion = hwVersion;
        this.hops = hops;
        this.roundTripTime = roundTripTime;
        this.qualityExt = qualityExt;
        this.qualityDev = qualityDev;
        this.online = online;
        this.battery = battery;
        this.dummy = dummy;
        this.oneWireDevices = oneWireDevices;
    }
}
