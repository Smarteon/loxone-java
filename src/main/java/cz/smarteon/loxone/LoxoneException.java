package cz.smarteon.loxone;

/**
 * Loxone exception.
 */
public class LoxoneException extends RuntimeException {
    public LoxoneException(String message) {
        super(message);
    }

    public LoxoneException(String message, Throwable cause) {
        super(message, cause);
    }
}
