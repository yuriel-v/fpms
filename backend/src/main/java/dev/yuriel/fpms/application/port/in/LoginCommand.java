package dev.yuriel.fpms.application.port.in;

/**
 * Input for {@link LoginUseCase#login(LoginCommand)} — the credentials
 * supplied by a user attempting to authenticate.
 *
 * <p>Carries the plaintext password; verification is delegated to a driven
 * adapter. Pure Java — no framework dependencies.
 *
 * @param email       the login email
 * @param rawPassword the plaintext password to verify
 */
public record LoginCommand(String email, String rawPassword) {
}
