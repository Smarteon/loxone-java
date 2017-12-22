package cz.smarteon.loxone.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.LoxoneUuids;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(defaultImpl = UnknownControl.class, use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = AlarmControl.NAME, value = AlarmControl.class)
})
public abstract class Control {

    @JsonProperty("uuidAction")
    protected LoxoneUuid uuid;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("states") @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    protected Map<String, LoxoneUuids> states;

    public LoxoneUuid getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Map<String, LoxoneUuids> getStates() {
        return states;
    }

    protected LoxoneUuids getCompulsoryState(String stateName) {
        if (states != null && states.containsKey(stateName)) {
            return states.get(stateName);
        } else {
            throw new IllegalStateException("Missing compulsory state " + stateName);
        }
    }
}
