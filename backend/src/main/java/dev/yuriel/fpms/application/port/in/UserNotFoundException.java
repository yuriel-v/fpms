package dev.yuriel.fpms.application.port.in;

/**
 * Thrown by {@link GetCurrentUserUseCase} when no user exists for the given
 * identity — e.g. a valid token whose subject has since been removed.
 * Unchecked so use cases need not declare it. Pure Java — no framework
 * dependencies.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
