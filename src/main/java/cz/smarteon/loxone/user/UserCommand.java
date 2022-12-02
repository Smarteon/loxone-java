package cz.smarteon.loxone.user;

import cz.smarteon.loxone.Codec;
import cz.smarteon.loxone.app.MiniserverType;
import cz.smarteon.loxone.message.LoxoneMessageCommand;
import cz.smarteon.loxone.message.LoxoneValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Represents user commands for loxone user.
 * @param <V> value type
 */
public class UserCommand<V extends LoxoneValue> extends LoxoneMessageCommand<V> {

    private static final String CONTROL_PREFIX = "jdev/sps";

    UserCommand(final String operation,
                final Class<V> valueType,
                final Boolean httpSupport
    ) {
        super(requireNonNull(operation, "operation can't be null"),
                Type.JSON, valueType, httpSupport, true, MiniserverType.KNOWN);
    }

    /**
     * Creates create user command.
     * Command creates a user with a given username, result will contain the uuid of the new user.
     * @throws NullPointerException user has to have name
     * @param user User object
     * @return create user command
     */
    @NotNull
    public static UserCommand<LoxUuidValue> createUser(final @NotNull User user) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getName(),"User name cannot be null");
        return genericUserCommand(CREATE_USER + user.getName(), LoxUuidValue.class);
    }

    /**
     * Creates delete user command.
     * Command deletes a user with a given uuid.
     * @throws NullPointerException user has to have UUID
     * @param user User object
     * @return create user command
     */
    @NotNull
    public static UserCommand<EmptyValue> deleteUser(final @NotNull User user) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        return genericUserCommand(DEL_USER + user.getUuid(), EmptyValue.class);
    }

    /**
     * Creates get users command.
     * Command a list of all configured users.
     * @return get users command
     */
    @NotNull
    public static UserCommand<UserListValue> getUsers() {
        return genericUserCommand(GET_USERS, UserListValue.class);
    }

    /**
     * Creates get user details command.
     * Command returns a json with the full user configuration.
     * @throws NullPointerException user has to have UUID
     * @param user User object
     * @return get user details command
     */
    @NotNull
    public static UserCommand<UserValue> getUserDetails(final @NotNull User user) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        return genericUserCommand(GET_USER_DETAILS + user.getUuid(), UserValue.class);
    }

    /**
     * Creates get user groups command.
     * Command returns all available user-groups and additional information.
     * @return get user groups command
     */
    @NotNull
    public static UserCommand<UserGroupListValue> getUserGroups() {
        return genericUserCommand(GET_USER_GROUPS, UserGroupListValue.class);
    }

    /**
     * Serializes User to JSON and creates add or edit user command.
     * Command edits existing user if UUID is provided, otherwise creates a new user. Returns 500 if user with provided
     * UUID does not exist.
     * @param user User object
     * @return add or edit user command
     */
    @NotNull
    public static UserCommand<UserValue> addOrEditUser(final @NotNull User user) throws IOException {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        return genericUserCommand(ADD_EDIT_USER + Codec.writeMessage(user), UserValue.class, false);
    }

    /**
     * Creates add user to group command.
     * Command adds user to specified usergroup.
     * @throws NullPointerException user and usergroup have to have UUID
     * @param user User object
     * @param userGroup UserGroup object
     * @return add user to group command
     */
    @NotNull
    public static UserCommand<EmptyValue> addUserToGroup(
            final @NotNull User user,
            final @NotNull UserGroup userGroup) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        requireNonNull(userGroup, "UserGroup cannot be null");
        requireNonNull(userGroup.getUuid(), "UserGroup Uuid cannot be null");
        return genericUserCommand(ADD_USER_TO_GROUP + user.getUuid() + "/" + userGroup.getUuid(), EmptyValue.class);
    }

    /**
     * Creates remove user from group command.
     * Command removes user from specified usergroup.
     * @throws NullPointerException user and usergroup have to have UUID
     * @param user User object
     * @param userGroup UserGroup object
     * @return remove user from group command
     */
    @NotNull
    public static UserCommand<EmptyValue> removeUserFromGroup(
            final @NotNull User user,
            final @NotNull UserGroup userGroup) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        requireNonNull(userGroup, "UserGroup cannot be null");
        requireNonNull(userGroup.getUuid(), "UserGroup Uuid cannot be null");
        return genericUserCommand(DEL_USER_FROM_GROUP + user.getUuid() + "/" + userGroup.getUuid(), EmptyValue.class);
    }

    /**
     * Creates add nfc to user command.
     * Command assigns specified NFC tag to user.
     * @throws NullPointerException user and nfcTag have to have UUID
     * @param user User object
     * @param nfc NfcTag object
     * @return add nfc to user command
     */
    @NotNull
    public static UserCommand<EmptyValue> addNfcToUser(
            final @NotNull User user,
            final @NotNull NfcTag nfc) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        requireNonNull(nfc, "nfc cannot be null");
        requireNonNull(nfc.getId(), "nfc Id cannot be null");
        return genericUserCommand(ADD_NFC_TO_USER + user.getUuid() + "/" + nfc.getId() + "/" + nfc.getName(), EmptyValue.class);
    }

    /**
     * Creates remove nfc from user command.
     * Command removes specified NFC tag from user.
     * @throws NullPointerException user and nfcTag have to have UUID
     * @param user User object
     * @param nfc NfcTag object
     * @return remove nfc from user command
     */
    @NotNull
    public static UserCommand<EmptyValue> removeNfcFromUser(
            final @NotNull User user,
            final @NotNull NfcTag nfc) {
        requireNonNull(user,"User cannot be null");
        requireNonNull(user.getUuid(),"User UUID cannot be null");
        requireNonNull(nfc, "Nfc cannot be null");
        requireNonNull(nfc.getId(), "Nfc Id cannot be null");
        return genericUserCommand(DEL_NFC_FROM_USER + user.getUuid() + "/" + nfc.getId(), EmptyValue.class);
    }

    /**
     * Creates generic user command resulting in {@link LoxoneValue}.
     * @param operation operation
     * @param httpSupport http support
     * @return user command
     */
    private static <V extends LoxoneValue> UserCommand<V> genericUserCommand(
            final @NotNull String operation,
            final @NotNull Class<V> valueType,
            final @NotNull Boolean httpSupport) {
        return new UserCommand<>(operation, valueType, httpSupport);
    }

    /**
     * Creates generic user command resulting in {@link LoxoneValue}.
     * @param operation operation
     * @return user command
     */
    private static <V extends LoxoneValue> UserCommand<V> genericUserCommand(final @NotNull String operation, final Class<V> valueType) {
        return genericUserCommand(operation, valueType, true);
    }

    /**
     * Returns command value type.
     * @param id command id
     * @return command value type
     */
    public static Class<?> getUserCommandValueType(final @NotNull String id) {
        if (id.contains(GET_USERS)) {
            return UserListValue.class;
        } else if (id.contains(GET_USER_DETAILS) || id.contains(ADD_EDIT_USER)) {
            return UserValue.class;
        } else if (id.contains(GET_USER_GROUPS)) {
            return UserGroupListValue.class;
        } else if (id.contains(CREATE_USER)) {
            return LoxUuidValue.class;
        }
        return EmptyValue.class;
    }

    @Override
    public String getCommand() {
        return CONTROL_PREFIX + "/" + super.getCommand();
    }

    private final static String CREATE_USER = "createuser/";
    private final static String DEL_USER = "deleteuser/";
    private final static String GET_USERS = "getuserlist2/";
    private final static String GET_USER_DETAILS = "getuser/";
    private final static String GET_USER_GROUPS = "getgrouplist/";
    private final static String ADD_EDIT_USER = "addoredituser/";
    private final static String ADD_USER_TO_GROUP = "assignusertogroup/";
    private final static String DEL_USER_FROM_GROUP = "removeuserfromgroup/";
    private final static String ADD_NFC_TO_USER = "addusernfc/";
    private final static String DEL_NFC_FROM_USER = "removeusernfc/";
}
