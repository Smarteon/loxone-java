package cz.smarteon.loxone.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoxoneConfig implements Serializable {

    private final Date lastModified;
    private final Map<LoxoneUuid, Control> controls;

    @JsonCreator
    public LoxoneConfig(@JsonProperty("lastModified")
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
                                    Date lastModified,
                        @JsonProperty("controls") Map<LoxoneUuid, Control> controls) {
        this.lastModified = lastModified;
        this.controls = controls;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Map<LoxoneUuid, Control> getControls() {
        return controls;
    }
}
