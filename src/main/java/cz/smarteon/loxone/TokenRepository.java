package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Repository for Loxone {@link Token} per {@link LoxoneProfile}.
 */
public interface TokenRepository {

    /**
     * Get the token for profile from repository.
     * @param profile Loxone profile
     * @return token for given profile, null if there is no such token
     */
    @Nullable
    Token getToken(final @NotNull LoxoneProfile profile);

    /**
     * Put the token for given profile to the repository.
     * @param profile Loxone profile
     * @param token token to be put
     */
    void putToken(final @NotNull LoxoneProfile profile, final @NotNull Token token);
}
