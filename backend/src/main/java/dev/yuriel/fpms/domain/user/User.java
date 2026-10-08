package dev.yuriel.fpms.domain.user;

import java.util.Currency;
import java.util.Objects;

/**
 * An account owner: identity, login email, hashed password, and the default
 * {@link Currency} new transactions start in.
 *
 * <p>The domain stores only the already-hashed password; computing the hash is
 * the responsibility of the authentication adapter. Immutable value — mutating
 * operations return a new instance. Pure Java — no framework dependencies.
 */
public record User(UserId id, String email, String passwordHash, Currency defaultCurrency) {

    public User {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(defaultCurrency, "defaultCurrency must not be null");
        email = normalizeEmail(email);
        passwordHash = requireNonBlank(passwordHash, "passwordHash");
    }

    public static User create(UserId id, String email, String passwordHash, Currency defaultCurrency) {
        return new User(id, email, passwordHash, defaultCurrency);
    }

    public static User create(UserId id, String email, String passwordHash, String defaultCurrencyCode) {
        return new User(id, email, passwordHash, toCurrency(defaultCurrencyCode));
    }

    public static User newUser(String email, String passwordHash, String defaultCurrencyCode) {
        return create(UserId.newId(), email, passwordHash, defaultCurrencyCode);
    }

    public User changeDefaultCurrency(Currency newCurrency) {
        return new User(this.id, this.email, this.passwordHash, newCurrency);
    }

    private static Currency toCurrency(String code) {
        Objects.requireNonNull(code, "defaultCurrency must not be null");
        return Currency.getInstance(code);
    }

    private static String normalizeEmail(String raw) {
        String email = requireNonBlank(raw, "email").toLowerCase();
        // Minimal structural guard; thorough validation lives at the web edge.
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1
                || email.indexOf('.', at) < 0) {
            throw new IllegalArgumentException("email is not a valid address");
        }
        return email;
    }

    private static String requireNonBlank(String raw, String field) {
        Objects.requireNonNull(raw, field + " must not be null");
        String trimmed = raw.strip();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }
}
