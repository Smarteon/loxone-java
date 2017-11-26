package cz.smarteon.loxone;

public class LoxoneException extends RuntimeException {
    public LoxoneException(String message) {
        super(message);
    }

    public LoxoneException(String message, Throwable cause) {
        super(message, cause);
    }
}
