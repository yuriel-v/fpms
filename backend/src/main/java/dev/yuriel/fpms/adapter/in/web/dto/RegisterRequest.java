package dev.yuriel.fpms.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Inbound web DTO for {@code POST /api/auth/register}.
 *
 * <p>Carries the raw registration details plus Jakarta Bean Validation
 * constraints applied at the web edge before mapping to a
 * {@code RegisterCommand}.
 *
 * @param email           the desired login email (must be a valid address)
 * @param password        the plaintext password
 * @param defaultCurrency the ISO 4217 currency code (3 uppercase letters)
 */
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$", message = "defaultCurrency must be a 3-letter ISO 4217 code")
        String defaultCurrency) {
}
