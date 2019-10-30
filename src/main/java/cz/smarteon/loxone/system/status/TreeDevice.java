package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TreeDevice extends Device {

    private final String place;
    private final String installation;
    private final String version;
    private final Boolean online;
    private final String lastReceived; // TODO time
    private final Integer timeDiff; // TODO semantics??
    private final Boolean dummy;

    @JsonCreator
    TreeDevice(@JsonProperty("Code") final String code,
               @JsonProperty("Name") final String name,
               @JsonProperty("Serial") final String serialNumber,
               @JsonProperty("Place") final String place,
               @JsonProperty("Inst") final String installation,
               @JsonProperty("Version") final String version,
               @JsonProperty("Online") final Boolean online,
               @JsonProperty("LastReceived") final String lastReceived,
               @JsonProperty("TimeDiff") final Integer timeDiff,
               @JsonProperty("DummyDev") final Boolean dummy) {
        super(code, name, serialNumber);
        this.place = place;
        this.installation = installation;
        this.version = version;
        this.online = online;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
        this.dummy = dummy;
    }

    public String getPlace() {
        return place;
    }

    public String getInstallation() {
        return installation;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    public String getLastReceived() {
        return lastReceived;
    }

    public Integer getTimeDiff() {
        return timeDiff;
    }

    public boolean isDummy() {
        return Boolean.TRUE.equals(dummy);
    }
}
