package dev.yuriel.fpms.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Inbound web DTO for {@code POST /api/auth/login}.
 *
 * @param email    the login email
 * @param password the plaintext password
 */
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}
