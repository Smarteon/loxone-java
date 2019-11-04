package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Token;

class TokenState {

    private static final long MAX_SECONDS_TO_EXPIRE = 60L;
    private static final long REFRESH_THRESHOLD = 300L;

    private final Long secondsToExpire;

    TokenState(final Token token) {
        this.secondsToExpire = token != null ? token.getSecondsToExpire() : null;
    }

    boolean isExpired() {
        return secondsToExpire == null || secondsToExpire <= MAX_SECONDS_TO_EXPIRE;
    }

    boolean needsRefresh() {
        return secondsToExpire != null
                && REFRESH_THRESHOLD >= secondsToExpire
                && secondsToExpire > MAX_SECONDS_TO_EXPIRE;
    }

    boolean isUsable() {
        return !isExpired() && !needsRefresh();
    }


}
