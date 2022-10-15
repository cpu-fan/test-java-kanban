package tasktracker.exceptions;

public class NonExistentTaskException extends RuntimeException {
    public NonExistentTaskException(String message) {
        super(message);
    }

    public NonExistentTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
