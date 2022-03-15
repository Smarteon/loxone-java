package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Token;

/**
 * Helper class for token state diagnostics.
 */
class TokenState {

    private static final long MAX_SECONDS_TO_EXPIRE = 60L;
    private static final long REFRESH_THRESHOLD = 300L;

    private final Long secondsToExpire;

    /**
     * Creates the state from given token.
     * @param token token to get the state from, can be null
     */
    TokenState(final Token token) {
        this.secondsToExpire = token != null && token.isFilled() ? token.getSecondsToExpire() : null;
    }

    /**
     * Check if the token expired or is close to expiry.
     * @return true if token is not set or is close to expire, false otherwise
     */
    boolean isExpired() {
        return secondsToExpire == null || secondsToExpire <= MAX_SECONDS_TO_EXPIRE;
    }

    /**
     * Check if the token needs to be refreshed and is still valid in order to be refreshed.
     * When {@link #isExpired()} returns true, this method will return false.
     * @return true if token is close to expiry but not yet too close, false otherwise
     */
    boolean needsRefresh() {
        return secondsToExpire != null
                && REFRESH_THRESHOLD >= secondsToExpire
                && secondsToExpire > MAX_SECONDS_TO_EXPIRE;
    }

    /**
     * Number of seconds remaining until {@link #needsRefresh()} become true.
     * @return number of seconds till the token needs refresh
     */
    long secondsToRefresh() {
        return secondsToExpire == null || secondsToExpire < REFRESH_THRESHOLD ? 0 : secondsToExpire - REFRESH_THRESHOLD;
    }

    /**
     * Check whether the token is still valid and doesn't need refresh.
     * @return true if the token is not expired neither needs refresh, false otherwise
     */
    boolean isUsable() {
        return !isExpired() && !needsRefresh();
    }
}
