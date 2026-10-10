package dev.yuriel.fpms.application.port.in;

/**
 * Thrown by {@link LoginUseCase} when authentication fails — the email is
 * unknown or the password does not match. Deliberately does not distinguish
 * the two cases, to avoid leaking which emails are registered. Unchecked so
 * use cases need not declare it. Pure Java — no framework dependencies.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
