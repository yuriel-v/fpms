package dev.yuriel.fpms.adapter.out.security;

import dev.yuriel.fpms.application.port.out.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Driven (security) adapter implementing the outbound {@link PasswordHasher}
 * port using Spring Security's {@link BCryptPasswordEncoder}.
 *
 * <p>The application layer depends only on the {@link PasswordHasher}
 * abstraction; the BCrypt choice is confined to this adapter.
 */
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }
}
