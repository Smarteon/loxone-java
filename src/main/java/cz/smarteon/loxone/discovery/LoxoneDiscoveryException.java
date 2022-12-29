package cz.smarteon.loxone.discovery;

import cz.smarteon.loxone.LoxoneException;

/**
 * Loxone discovery exception.
 */
public class LoxoneDiscoveryException extends LoxoneException {

    public LoxoneDiscoveryException(final String message) {
        super((message));
    }

    public LoxoneDiscoveryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
