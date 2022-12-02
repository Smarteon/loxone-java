package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.message.LoxoneValue;
import lombok.Getter;

import java.io.IOException;

/**
 * {@link LoxoneValue} where the JSON string is expected to encode {@link User}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class UserValue implements LoxoneValue {

    private final User user;

    @JsonCreator
    protected UserValue(String user) throws IOException {
        this.user = Codec.readMessage(user, User.class);
    }
}
