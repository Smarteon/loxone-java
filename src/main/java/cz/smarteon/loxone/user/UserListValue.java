package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.LoxoneUuid;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode a list of users.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class UserListValue implements LoxoneValue {

    private final List<UserListElement> users;

    @JsonCreator
    protected UserListValue(String users) throws IOException {
        this.users = Codec.readList(users, UserListElement.class);
    }

    /**
     * Helper class to properly serialize/deserialize {@link UserListValue}.
     */
    public static class UserListElement extends UserBase {
        @JsonCreator
        protected UserListElement(@JsonProperty("name") String name,
                                  @JsonProperty("uuid") LoxoneUuid uuid,
                                  @JsonProperty("isAdmin") boolean isAdmin,
                                  @JsonProperty("userState") User.UserState userState,
                                  @JsonProperty("representsControl") boolean representsControl) {
            super(name, uuid, isAdmin, userState, representsControl);
        }
    }
}
