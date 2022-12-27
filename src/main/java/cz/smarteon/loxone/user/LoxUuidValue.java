package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * {@link LoxoneValue} where the JSON string is expected to encode {@link LoxoneUuid}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
public class LoxUuidValue implements LoxoneValue {

    private final LoxoneUuid uuid;

    @JsonCreator
    public LoxUuidValue(String uuid) {
        this.uuid = new LoxoneUuid(uuid);
    }
}
