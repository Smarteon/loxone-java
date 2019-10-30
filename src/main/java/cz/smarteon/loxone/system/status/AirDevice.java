package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirDevice extends Device {

    private final String type;
    private final String place;
    private final String installation;
    private final String lastReceived; // TODO time
    private final Integer timeDiff; // TODO semantics??
    private final String version;
    private final String minVersion; // TODO semantics??
    private final String hwVersion;
    private final Integer hops;
    private final Integer roundTripTime;
    private final String qualityExt; // TODO semantics??
    private final String qualityDev; // TODO semantics??
    private final Boolean online;
    private final Integer battery;
    private final Boolean dummy;

    @JsonCreator
    AirDevice(@JsonProperty("Type") final String type,
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
              @JsonProperty("DummyDev") final Boolean dummy) {
        super(code, name, serialNumber);
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
    }

    public String getType() {
        return type;
    }

    public String getPlace() {
        return place;
    }

    public String getInstallation() {
        return installation;
    }

    public String getLastReceived() {
        return lastReceived;
    }

    public Integer getTimeDiff() {
        return timeDiff;
    }

    public String getVersion() {
        return version;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public String getHwVersion() {
        return hwVersion;
    }

    public Integer getHops() {
        return hops;
    }

    public Integer getRoundTripTime() {
        return roundTripTime;
    }

    public String getQualityExt() {
        return qualityExt;
    }

    public String getQualityDev() {
        return qualityDev;
    }

    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    public Integer getBattery() {
        return battery;
    }

    public boolean getDummy() {
        return Boolean.TRUE.equals(dummy);
    }
}
