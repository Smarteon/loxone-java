package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        this.userGroups = Arrays.asList(Codec.readMessage(userGroups, UserGroup[].class));
    }
}
