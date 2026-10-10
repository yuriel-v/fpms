package dev.yuriel.fpms.application.port.out;

/**
 * Thrown by a {@link TokenService} when a token fails validation — bad
 * signature, malformed, or expired. Unchecked so use cases need not handle it
 * explicitly. Pure Java — no framework dependencies.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
