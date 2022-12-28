package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneNotDocumented;
import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

/**
 * Represents a user and their rights.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiniserverUser {

    private final String name;
    private final LoxoneUuid uuid;
    private final boolean admin;
    private final boolean changePassword;

    @LoxoneNotDocumented
    private final BigInteger rights;

    @JsonCreator
    public MiniserverUser(@JsonProperty("name") String name,
                          @JsonProperty("uuid") LoxoneUuid uuid,
                          @JsonProperty("isAdmin") boolean admin,
                          @JsonProperty("changePassword") boolean changePassword,
                          @JsonProperty("userRights") @LoxoneNotDocumented BigInteger rights) {
        this.name = name;
        this.uuid = uuid;
        this.admin = admin;
        this.changePassword = changePassword;
        this.rights = rights;
    }

    public String getName() {
        return name;
    }

    public LoxoneUuid getUuid() {
        return uuid;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean canChangePassword() {
        return changePassword;
    }

    @LoxoneNotDocumented
    @Nullable
    public BigInteger getRights() {
        return rights;
    }
}
