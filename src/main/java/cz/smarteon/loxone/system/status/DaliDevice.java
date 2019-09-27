package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DaliDevice extends Device {

    private final Boolean error;

    @JsonCreator
    DaliDevice(@JsonProperty("Code") final String code,
                  @JsonProperty("Name") final String name,
                  @JsonProperty("Serial") final String serialNumber,
                  @JsonProperty("Error") final Boolean error) {
        super(code, name, serialNumber);
        this.error = error;
    }

    public Boolean getError() {
        return error;
    }
}
