package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents loxone profile - endpoint and credentials.
 */
public final class LoxoneProfile {

    private final LoxoneEndpoint endpoint;
    private final String username;
    private final String password;
    private final String visuPassword;

    /**
     * Creates new instance of given endpoint, username and password.
     * @param endpoint endpoint of the miniserver
     * @param username miniserver username
     * @param password miniserver password
     */
    public LoxoneProfile(
            final @NotNull LoxoneEndpoint endpoint,
            final @NotNull String username,
            final @NotNull String password
    ) {
        this(endpoint, username, password, null);
    }

    /**
     * Creates new instance of given endpoint, username, password and visualization password.
     * @param endpoint endpoint of the miniserver
     * @param username miniserver username
     * @param password miniserver password
     * @param visuPassword miniserver visualization password
     */
    public LoxoneProfile(
            final @NotNull LoxoneEndpoint endpoint,
            final @NotNull String username,
            final @NotNull String password,
            final @Nullable String visuPassword
    ) {
        this.endpoint = requireNonNull(endpoint);
        this.username = requireNonNull(username);
        this.password = requireNonNull(password);
        this.visuPassword = visuPassword;
    }

    /**
     * @return miniserver endpoint
     */
    @NotNull
    public LoxoneEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @return miniserver username
     */
    @NotNull
    public String getUsername() {
        return username;
    }

    /**
     * @return miniserver password
     */
    @NotNull
    public String getPassword() {
        return password;
    }

    /**
     * @return miniserver visualization password
     */
    @Nullable
    public String getVisuPassword() {
        return visuPassword;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoxoneProfile that = (LoxoneProfile) o;
        return endpoint.equals(that.endpoint)
                && username.equals(that.username)
                && password.equals(that.password)
                && Objects.equals(visuPassword, that.visuPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, username, password, visuPassword);
    }

    @Override
    public String toString() {
        return username + "@" + endpoint;
    }
}
