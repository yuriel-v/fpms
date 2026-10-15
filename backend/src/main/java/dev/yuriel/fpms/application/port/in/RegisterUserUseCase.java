package dev.yuriel.fpms.application.port.in;

/**
 * Inbound port for registering a new account owner — an inbound adapter drives this to create a
 * user and obtain an authenticated session in one step.
 *
 * <p>Implemented by an application service; the application depends only on this abstraction. Pure
 * Java + domain types — no framework dependencies.
 */
public interface RegisterUserUseCase {

  /**
   * Registers a new user and issues an authentication token.
   *
   * @param command the registration details
   * @return the issued token and the new user's identity
   * @throws EmailAlreadyInUseException if a user with the given email already exists
   */
  AuthResult register(RegisterCommand command);
}
