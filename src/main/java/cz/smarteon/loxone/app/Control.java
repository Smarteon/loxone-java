package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.LoxoneUuids;

import java.util.Map;

/**
 * Base class for all the controls in config
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(defaultImpl = UnknownControl.class, use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = AlarmControl.NAME, value = AlarmControl.class),
        @JsonSubTypes.Type(name = SwitchControl.NAME, value = SwitchControl.class)
})
public abstract class Control {

    @JsonProperty("uuidAction")
    protected LoxoneUuid uuid;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("isSecured")
    protected boolean secured;

    @JsonProperty("states") @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    protected Map<String, LoxoneUuids> states;

    /**
     * UUID of this control, should be unique
     * @return control UUID
     */
    public LoxoneUuid getUuid() {
        return uuid;
    }

    /**
     * Control name - usually localized, non unique
     * @return control name
     */
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
     * Control states map
     * @return control states
     */
    public Map<String, LoxoneUuids> getStates() {
        return states;
    }

    /**
     * Helper to get state by name, which should be in this control.
     * @param stateName name of the desired state
     * @return UUID of desired state if there is such
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
