package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import cz.smarteon.loxone.LoxoneNotDocumented;
import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * Base class for User
 */
@Getter
public abstract class UserBase {

    /**
     * Enum class containing constants for userState
     */
    @RequiredArgsConstructor
    public enum UserState {
        ENABLED(0),
        DISABLED(1),
        ENABLED_UNTIL(2),
        ENABLED_FROM(3),
        TIMESPAN(4),
        UNKNOWN(-1);

        @Getter
        private final int value;

        @JsonCreator
        public static User.UserState fromValue(final int value) {
            UserState result = UNKNOWN;
            for (UserState state : values()) {
                if (state.value == value)
                    result = state;
            }

            return result;
        }
    }

    /**
     * Name of this user, should be unique
     */
    private final String name;

    /**
     * UUID of this user, should be unique
     */
    private final LoxoneUuid uuid;

    /**
     * Whether user is an admin
     */
    private final boolean isAdmin;

    /**
     * Indicates whether a user is active or disabled.
     * <pre>
     *  0 = enabled, without time limitations.
     *  1 = disabled.
     *  2 = enabled until, disabled after that point in time.
     *  3 = enabled from, disabled before that point in time.
     *  4 = timespan, only enabled in between those points in time.</pre>
     */
    @Setter
    @JsonSerialize(using = UserStateSerializer.class)
    private UserState userState;

    @LoxoneNotDocumented
    @JsonIgnore
    private final boolean representsControl;

    protected UserBase (LoxoneUuid uuid){
        this.uuid = uuid;
        this.name = null;
        this.isAdmin = false;
        this.representsControl = false;
    }

    protected UserBase (String name){
        this.name = name;
        this.uuid = null;
        this.isAdmin = false;
        this.representsControl = false;
    }

    protected UserBase (LoxoneUuid uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.isAdmin = false;
        this.representsControl = false;
    }

    protected UserBase(
            LoxoneUuid uuid,
            String name,
            UserState userState) {
        this.uuid = uuid;
        this.name = name;
        this.userState = userState;
        this.isAdmin = false;
        this.representsControl = false;
    }

    protected UserBase(
            LoxoneUuid uuid,
            String name,
            UserState userState,
            boolean isAdmin) {
        if (uuid == null && name == null) throw new NullPointerException("uuid and name can't be both null");
        this.uuid = uuid;
        this.name = name;
        this.userState = userState;
        this.isAdmin = isAdmin;
        this.representsControl = false;
    }

    protected UserBase(
            String name,
            LoxoneUuid uuid,
            boolean isAdmin,
            UserState userState,
            boolean representsControl) {
        this.name = name;
        this.uuid = uuid;
        this.isAdmin = isAdmin;
        this.userState = userState;
        this.representsControl = representsControl;
    }

    public static class UserStateSerializer extends StdSerializer<UserState> {

        public UserStateSerializer() {
            this(null);
        }

        public UserStateSerializer(Class<UserState> t) {
            super(t);
        }

        @Override
        public void serialize(UserState value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.getValue());
        }
    }
}
