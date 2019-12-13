package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneException;
import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Represents the loxone application as used in user interface.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoxoneApp implements Serializable {

    private final Date lastModified;
    private final MiniserverInfo miniserverInfo;
    private final Map<LoxoneUuid, Control> controls;

    @JsonCreator
    public LoxoneApp(@JsonProperty("lastModified") Date lastModified,
                     @JsonProperty("msInfo") MiniserverInfo miniserverInfo,
                     @JsonProperty("controls") Map<LoxoneUuid, Control> controls) {
        this.lastModified = requireNonNull(lastModified, "lastModified can't be null");
        this.miniserverInfo = requireNonNull(miniserverInfo, "miniserverInfo can't be null");
        this.controls = requireNonNull(controls, "controls can't be null");
    }

    @NotNull
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Gets this miniserver info.
     *
     * @return miniserver info as {@link MiniserverInfo}
     */
    @NotNull
    public MiniserverInfo getMiniserverInfo() {
        return miniserverInfo;
    }

    @NotNull
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
    @Nullable
    public <T extends Control> T getControl(final @NotNull Class<T> type) {
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
    @NotNull
    public <T extends Control> Collection<T> getControls(final @NotNull Class<T> type) {
        requireNonNull(type, "control type can't be null");
        final List<T> found = new ArrayList<>();
        for (Control c : controls.values()) {
            if ( type.isAssignableFrom(c.getClass())) {
                found.add((T) c);
            }
        }
        return found;
    }

    /**
     * Get single (if any) control of given type and name
     * @param name control name
     * @param type control type
     * @param <T> class of control type
     * @return the only control of given name and type, or null if no such control exists
     * @throws cz.smarteon.loxone.LoxoneException if more than one control of given name and type exists
     */
    @JsonIgnore
    @Nullable
    public <T extends Control> T getControl(final @NotNull String name, final @NotNull Class<T> type) {
        requireNonNull(name, "control name can't be null");
        T found = null;
        for (T control : getControls(type)) {
            if (name.equals(control.getName())) {
                if (found != null) {
                    throw new LoxoneException("More than one control of name " + name + " and type " + type.getSimpleName() + " found!");
                } else {
                    found = control;
                }
            }
        }
        return found;
    }
}
