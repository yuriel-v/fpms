package dev.yuriel.fpms.application.port.in;

/**
 * Inbound port for authenticating an existing user — an inbound adapter drives
 * this to verify credentials and obtain an authenticated session.
 *
 * <p>Implemented by an application service; the application depends only on
 * this abstraction. Pure Java + domain types — no framework dependencies.
 */
public interface LoginUseCase {

    /**
     * Authenticates a user and issues an authentication token.
     *
     * @param command the login credentials
     * @return the issued token and the authenticated user's identity
     * @throws InvalidCredentialsException if the email is unknown or the
     *                                     password does not match
     */
    AuthResult login(LoginCommand command);
}
