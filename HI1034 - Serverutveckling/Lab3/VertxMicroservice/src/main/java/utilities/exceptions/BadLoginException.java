package utilities.exceptions;

public class BadLoginException extends RuntimeException {
    public BadLoginException(String message) {
        super(message);
    }
}
