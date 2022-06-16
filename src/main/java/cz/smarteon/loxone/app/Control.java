package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.LoxoneUuids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Base class for all the controls in loxone application
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(defaultImpl = UnknownControl.class, use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = AlarmControl.NAME, value = AlarmControl.class),
        @JsonSubTypes.Type(name = SwitchControl.NAME, value = SwitchControl.class),
        @JsonSubTypes.Type(name = PresenceControl.NAME, value = PresenceControl.class),
        @JsonSubTypes.Type(name = TechnicalAlarmControl.NAME, value = TechnicalAlarmControl.class),
        @JsonSubTypes.Type(name = DigitalInfoControl.NAME, value = DigitalInfoControl.class),
        @JsonSubTypes.Type(name = AnalogInfoControl.NAME, value = AnalogInfoControl.class)
})
public abstract class Control {

    @JsonProperty(value = "uuidAction", required = true)
    protected LoxoneUuid uuid;

    @JsonProperty(value = "name", required = true)
    protected String name;

    @JsonProperty(value = "isSecured")
    protected boolean secured;

    @JsonProperty(value = "type")
    protected String type;

    @JsonProperty(value = "defaultRating")
    protected int rating;

    @JsonProperty("states") @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    protected Map<String, LoxoneUuids> states;

    /**
     * UUID of this control, should be unique
     * @return control UUID
     */
    @NotNull
    public LoxoneUuid getUuid() {
        return uuid;
    }

    /**
     * Control name - usually localized, non unique
     * @return control name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Whether this control is secured by visualization password.
     * @return true when this control is secured, false otherwise
     */
    public boolean isSecured() {
        return secured;
    }

    /**
     * Control type e.g. Jalousie, Daytimer, … empty type should not be visualized
     * @return control type
     */
    @NotNull
    public String getType() {
        return type;
    }

    /**
     * Based on this number, controls are sorted in the UI
     * @return control rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Control states map
     * @return control states
     */
    @Nullable
    public Map<String, LoxoneUuids> getStates() {
        return states;
    }

    /**
     * Helper to get state by name, which should be in this control.
     * Usually, the state contains only one uuid - use {@link LoxoneUuids#only()} to fetch it.
     *
     * @param stateName name of the desired state
     * @return UUIDs of desired state if there are some
     * @throws IllegalStateException in case there is no state of desired name
     */
    protected LoxoneUuids getCompulsoryState(final String stateName) {
        if (states != null && states.containsKey(stateName)) {
            return states.get(stateName);
        } else {
            throw new IllegalStateException("Missing compulsory state " + stateName);
        }
    }

}
