package dev.yuriel.fpms.application.port.in;

/**
 * Input for {@link RegisterUserUseCase#register(RegisterCommand)} — the raw
 * details supplied by a prospective account owner.
 *
 * <p>Carries the plaintext password; hashing is delegated to a driven adapter.
 * Pure Java — no framework dependencies.
 *
 * @param email               the desired login email
 * @param rawPassword         the plaintext password to be hashed and stored
 * @param defaultCurrencyCode the ISO 4217 code for the user's default currency
 */
public record RegisterCommand(String email, String rawPassword, String defaultCurrencyCode) {
}
