package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.smarteon.loxone.LoxoneTime;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents Loxone authentication token.
 */
public class Token implements LoxoneValue {

    private final String token;
    private final byte[] key;
    private final int validUntil;
    private final int rights;
    private final boolean unsecurePassword;

    @JsonCreator
    public Token(@JsonProperty("token") final String token,
                 @JsonProperty("key") @JsonDeserialize(using = HexDeserializer.class) final byte[] key,
                 @JsonProperty("validUntil") final int validUntil,
                 @JsonProperty("tokenRights") final int rights,
                 @JsonProperty("unsecurePass") final boolean unsecurePassword) {
        this.token = token;
        this.key = key;
        this.validUntil = validUntil;
        this.rights = rights;
        this.unsecurePassword = unsecurePassword;
    }

    /**
     * The actual token value. May be null in case of response to refresh token or authwithtoken.
     * @return token
     */
    @Nullable
    public String getToken() {
        return token;
    }

    /**
     * The token key value. May be null in case of response to refresh token or authwithtoken.
     * @return token key
     */
    @JsonSerialize(using = HexSerializer.class)
    @Nullable
    public byte[] getKey() {
        return key;
    }

    /**
     * Seconds since loxone epoch (1.1.2009) to which the token is valid.
     * @return valid until seconds
     */
    public int getValidUntil() {
        return validUntil;
    }

    @JsonProperty("tokenRights")
    public int getRights() {
        return rights;
    }

    @JsonProperty("unsecurePass")
    public boolean isUnsecurePassword() {
        return unsecurePassword;
    }

    /**
     * Seconds remaining to token expiry.
     * @return seconds to expire
     */
    @JsonIgnore
    public long getSecondsToExpire() {
        return LoxoneTime.getUnixEpochSeconds(validUntil) - (System.currentTimeMillis() / 1000);
    }

    /**
     * Get human understandable date and time of validity in system default time zone.
     * @return valid until date and time
     */
    @JsonIgnore
    public LocalDateTime getValidUntilDateTime() {
        return LoxoneTime.getLocalDateTime(validUntil);
    }

    /**
     * Merges the given token to this one and returns the merged token. The {@link #token} and {@link #key} are taken
     * from given token only if they are not null, otherwise the values from this token are used. Other properties are
     * always taken from given token.
     *
     * @param other token to merge
     * @return new merged token
     */
    public Token merge(final Token other) {
        return this.equals(other) ? this : new Token(
                other.token != null ? other.token : token,
                other.key != null ? other.key : key,
                other.validUntil,
                other.rights,
                other.unsecurePassword
        );
    }

    /**
     * Checks whether all authentication needed fields are filled (no null).
     * @return true when @{link {@link #token} and {@link #key} are not null, false otherwise
     */
    @JsonIgnore
    public boolean isFilled() {
        return token != null && key != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return validUntil == token1.validUntil &&
                rights == token1.rights &&
                unsecurePassword == token1.unsecurePassword &&
                Objects.equals(token, token1.token) &&
                Arrays.equals(key, token1.key);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(token, validUntil, rights, unsecurePassword);
        result = 31 * result + Arrays.hashCode(key);
        return result;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", key=" + Arrays.toString(key) +
                ", validUntil=" + validUntil +
                ", rights=" + rights +
                ", unsecurePassword=" + unsecurePassword +
                '}';
    }
}
