package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;

/**
 * Represents Category
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private final LoxoneUuid uuid;

    private final String name;

    @JsonCreator
    public Category(@JsonProperty(value = "uuid", required = true) LoxoneUuid uuid,
                    @JsonProperty(value = "name", required = true) String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public LoxoneUuid getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
