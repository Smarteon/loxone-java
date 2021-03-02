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
@JsonTypeInfo(defaultImpl = UnknownControl.class, use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = AlarmControl.NAME, value = AlarmControl.class),
        @JsonSubTypes.Type(name = AudioZoneControl.NAME, value = AudioZoneControl.class),
        @JsonSubTypes.Type(name = DimmerControl.NAME, value = DimmerControl.class),
        @JsonSubTypes.Type(name = InfoOnlyAnalogControl.NAME, value = InfoOnlyAnalogControl.class),
        @JsonSubTypes.Type(name = InfoOnlyDigitalControl.NAME, value = InfoOnlyDigitalControl.class),
        @JsonSubTypes.Type(name = IRoomControllerV2Control.NAME, value = IRoomControllerV2Control.class),
        @JsonSubTypes.Type(name = JalousieControl.NAME, value = JalousieControl.class),
        @JsonSubTypes.Type(name = LightControllerControl.NAME, value = LightControllerControl.class),
        @JsonSubTypes.Type(name = LightControllerV2Control.NAME, value = LightControllerV2Control.class),
        @JsonSubTypes.Type(name = SwitchControl.NAME, value = SwitchControl.class),
        @JsonSubTypes.Type(name = TextStateControl.NAME, value = TextStateControl.class),
        @JsonSubTypes.Type(name = TextInputControl.NAME, value = TextInputControl.class),
        @JsonSubTypes.Type(name = PresenceControl.NAME, value = PresenceControl.class),
        @JsonSubTypes.Type(name = PushbuttonControl.NAME, value = PushbuttonControl.class),
        @JsonSubTypes.Type(name = PresenceDetectorControl.NAME, value = PresenceDetectorControl.class),
        @JsonSubTypes.Type(name = RadioControl.NAME, value = RadioControl.class),
        @JsonSubTypes.Type(name = TechnicalAlarmControl.NAME, value = TechnicalAlarmControl.class),
        @JsonSubTypes.Type(name = WindowMonitorControl.NAME, value = WindowMonitorControl.class)
})
public abstract class Control {

    @JsonProperty(value = "uuidAction", required = true)
    protected LoxoneUuid uuid;

    @JsonProperty(value = "name", required = true)
    protected String name;

    @JsonProperty(value = "isSecured")
    protected boolean secured;

    @JsonProperty("details") @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    protected Map<String, Object> details;

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
     * Control details map
     * @return control states
     */
    @Nullable
    public Map<String, Object> getDetails() {
        return details;
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
     * Helper to get detail by name, which should be in this control.
     *
     * @param detailName name of the desired state
     * @return UUIDs of desired state if there are some
     * @throws IllegalStateException in case there is no state of desired name
     */
    protected Object getCompulsoryDetail(final String detailName) {
        if (details != null && details.containsKey(detailName)) {
            return details.get(detailName);
        } else {
            throw new IllegalStateException("Missing compulsory detail " + detailName);
        }
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
