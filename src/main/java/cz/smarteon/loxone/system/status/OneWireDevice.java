package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OneWireDevice extends Device {

    private final String family; // TODO semantics??
    private final String lastReceived; // TODO time
    private final String timeDiff; // TODO semantics??

    @JsonCreator
    OneWireDevice(@JsonProperty("Code") final String code,
                  @JsonProperty("Name") final String name,
                  @JsonProperty("Serial") final String serialNumber,
                  @JsonProperty("Family") final String family,
                  @JsonProperty("LastReceived") final String lastReceived,
                  @JsonProperty("TimeDiff") final String timeDiff) {
        super(code, name, serialNumber);
        this.family = family;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
    }

    public String getFamily() {
        return family;
    }

    public String getLastReceived() {
        return lastReceived;
    }

    public String getTimeDiff() {
        return timeDiff;
    }
}
