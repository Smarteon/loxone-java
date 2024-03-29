package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode a list of user groups.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
public class UserGroupListValue implements LoxoneValue {

    private final List<UserGroup> userGroups;

    @JsonCreator
    public UserGroupListValue(String userGroups) throws IOException {
        this.userGroups = Codec.readList(userGroups, UserGroup.class);
    }
}
