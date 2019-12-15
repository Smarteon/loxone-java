package cz.smarteon.loxone.message;

import java.util.function.Function;

/**
 * Represents {@link Token}'s permission type necessary for token acquire.
 */
public enum TokenPermissionType {

    /**
     * WEB permission - short token validity (lasts for hours)
     */
    WEB(2),

    /**
     * APP permission - long token validity (lasts for weeks)
     */
    APP(4);

    private final int id;

    TokenPermissionType(final int id) {
        this.id = id;
    }

    /**
     * Token permission ID used in {@link EncryptedCommand#getToken(String, String, TokenPermissionType, String, String, Function)} request.
     * @return permission ID
     */
    public int getId() {
        return id;
    }
}
