package cz.smarteon.loxone.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneException;
import cz.smarteon.loxone.LoxoneUuid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * @param type control type to get
     * @param <T> class of control type
     * @return the only control of given type, or null if no such control exists
     * @throws cz.smarteon.loxone.LoxoneException if more than one control of given type exists
     */
    @JsonIgnore
    public <T extends Control> T getControl(Class<T> type) {
        final Collection<T> found = getControls(type);
        switch (found.size()) {
            case 0:
                return null;
            case 1:
                return found.iterator().next();
            default:
                throw new LoxoneException("More than one control of type " + type.getSimpleName() + " found!");
        }
    }

    /**
     * @param type control type to get
     * @param <T> class of control type
     * @return collection of found control for given type (may be empty)
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <T extends Control> Collection<T> getControls(Class<T> type) {
        Objects.requireNonNull(type, "control type can't be null");
        final List<T> found = new ArrayList<>();
        for (Control c : controls.values()) {
            if ( type.isAssignableFrom(c.getClass())) {
                found.add((T) c);
            }
        }
        return found;
    }
}
