package dev.yuriel.fpms.application.port.in;

/**
 * Thrown by {@link RegisterUserUseCase} when registration is attempted with an email that already
 * belongs to an existing user. Unchecked so use cases need not declare it. Pure Java — no framework
 * dependencies.
 */
public class EmailAlreadyInUseException extends RuntimeException {

  public EmailAlreadyInUseException(String message) {
    super(message);
  }

  public EmailAlreadyInUseException(String message, Throwable cause) {
    super(message, cause);
  }
}
