package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MiniserverUser {

    private final String name;
    private final LoxoneUuid uuid;
    private final boolean admin;
    private final boolean changePassword;
    private final Integer rights;

    @JsonCreator
    public MiniserverUser(@JsonProperty("name") String name,
                          @JsonProperty("uuid") LoxoneUuid uuid,
                          @JsonProperty("isAdmin") boolean admin,
                          @JsonProperty("changePassword") boolean changePassword,
                          @JsonProperty("userRights") Integer rights) {
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

    public Integer getRights() {
        return rights;
    }
}
