package cz.smarteon.loxone.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.smarteon.loxone.LoxoneTime;
import cz.smarteon.loxone.LoxoneUuid;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Represents User
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class User extends UserBase{

    /**
     * Provided as seconds since 1.1.2009 00:00:00
     */
    @Setter
    private int validFrom;

    /**
     * Provided as seconds since 1.1.2009 00:00:00
     */
    @Setter
    private int validUntil;

    /**
     * Groups this user is part of
     */
    @JsonProperty(value = "usergroups")
    @JsonSerialize(contentUsing = UserGroup.UserGroupSerializer.class)
    @Setter
    private @Nullable List<UserGroup> userGroups;

    /**
     * NFC tags associated with this user
     */
    @JsonSerialize(contentUsing = NfcTag.NfcTagSerializer.class)
    @Setter
    private @Nullable List<NfcTag> nfcTags;

    public User (@NotNull LoxoneUuid uuid){
        super(requireNonNull(uuid, "uuid can't be null"));
    }

    public User (@NotNull String name){
        super(requireNonNull(name, "name can't be null"));
    }

    public User (@NotNull LoxoneUuid uuid, @NotNull String name){
        super(requireNonNull(uuid, "uuid can't be null"), requireNonNull(name, "name can't be null"));
    }

    public User(
            @Nullable LoxoneUuid uuid,
            @Nullable String name,
            int validUntil,
            int validFrom,
            @NotNull UserState userState) {
        super(uuid, name, requireNonNull(userState, "userState can't be null"));
        if (uuid == null && name == null) throw new NullPointerException("uuid and name can't be both null");
        this.validUntil = validUntil;
        this.validFrom = validFrom;
    }

    @JsonCreator
    public User(
            @JsonProperty(value = "uuid") @Nullable LoxoneUuid uuid,
            @JsonProperty(value = "name") @Nullable String name,
            @JsonProperty(value = "isAdmin") boolean isAdmin,
            @JsonProperty(value = "validUntil") int validUntil,
            @JsonProperty(value = "validFrom") int validFrom,
            @JsonProperty(value = "userState") @Nullable UserState userState,
            @JsonProperty(value = "usergroups") @Nullable List<UserGroup> userGroups,
            @JsonProperty(value = "nfcTags") @Nullable List<NfcTag> nfcTags) {
        super(uuid, name, userState, isAdmin);
        this.validUntil = validUntil;
        this.validFrom = validFrom;
        this.userGroups = userGroups;
        this.nfcTags = nfcTags;
    }

    /**
     * Get human understandable date and time of user's start of validity in system default time zone.
     * @return valid from date and time
     */
    @JsonIgnore
    public LocalDateTime getValidFromDateTime() {
        return LoxoneTime.getLocalDateTime(validFrom);
    }

    /**
     * Get human understandable date and time of user's end of validity in system default time zone.
     * @return valid until date and time
     */
    @JsonIgnore
    public LocalDateTime getValidUntilDateTime() {
        return LoxoneTime.getLocalDateTime(validUntil);
    }
}
