package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Represents UserGroup
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UserGroup {

    /**
     * Enum class containing constants for usergroup type
     */
    @RequiredArgsConstructor
    public enum UserGroupType {
        NORMAL(0),
        @Deprecated
        DEPRECATED_ADMIN(1),
        ALL(2),
        NONE(3),
        ADMIN(4),
        UNKNOWN(-1);

        @Getter
        private final int value;

        @JsonCreator
        public static UserGroupType fromValue(final int value) {
            UserGroupType result = UNKNOWN;
            for (UserGroupType type : values()) {
                if (type.value == value)
                    result = type;
            }

            return result;
        }
    }

    /**
     * Enum class containing constants for usergroup rights
     */
    @RequiredArgsConstructor
    public enum UserGroupRights {
        NONE(0),
        WEB(1),
        CONFIG(4),
        FTP(8),
        TELNET(10),
        OPERATING_MODES(20),
        AUTOPILOT(40),
        EXPERT(80),
        USER_MANAGEMENT(100),
        ADMIN(4294967295L),
        UNKNOWN(-1);

        @Getter
        private final long value;

        @JsonCreator
        public static UserGroupRights fromValue(final long value) {
            UserGroupRights result = UNKNOWN;
            for (UserGroupRights rights : values()) {
                if (rights.value == value)
                    result = rights;
            }

            return result;
        }
    }

    /**
     * UUID of this usergroup, should be unique
     */
    private final @NotNull LoxoneUuid uuid;

    /**
     * Name of this usergroup, should be unique
     */
    private final @Nullable String name;

    /**
     * Description of this usergroup
     */
    @Setter
    private @Nullable String description;

    /**
     * Type of this usergroup
     * <pre>
     * 0 = Normal
     * 1 = Admin (deprecated)
     * 2 = All
     * 3 = None
     * 4 = AllAccess - Admin
     */
    @Setter
    private UserGroupType type;

    /**
     * Rights of this usergroup
     */
    @Setter
    private UserGroupRights userRights;

    public UserGroup(@NotNull LoxoneUuid uuid) {
        this.uuid = requireNonNull(uuid, "uuid can't be null");
        this.name = null;
        this.description = null;
    }

    public UserGroup(@NotNull LoxoneUuid uuid, @NotNull String name) {
        this.uuid = requireNonNull(uuid, "uuid can't be null");
        this.name = requireNonNull(name, "name can't be null");
        this.description = null;
    }

    @JsonCreator
    public UserGroup(
            @JsonProperty(value = "uuid", required = true) @NotNull LoxoneUuid uuid,
            @JsonProperty(value = "name") @Nullable String name,
            @JsonProperty(value = "description") @Nullable String description,
            @JsonProperty(value = "type") @Nullable UserGroupType type,
            @JsonProperty(value = "userRights") @Nullable UserGroupRights userRights) {
        this.uuid = requireNonNull(uuid, "uuid can't be null");
        this.name = name;
        this.description = description;
        this.type = type;
        this.userRights = userRights;
    }

    public static class UserGroupSerializer extends StdSerializer<UserGroup> {

        public UserGroupSerializer() {
            this(null);
        }

        public UserGroupSerializer(Class<UserGroup> t) {
            super(t);
        }

        @Override
        public void serialize(UserGroup value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.getUuid().toString());
        }
    }
}
