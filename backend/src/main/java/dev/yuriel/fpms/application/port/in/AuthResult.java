package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.user.UserId;

/**
 * Result of a successful authentication (registration or login) — carries the issued token plus the
 * basic identity an inbound adapter needs to build its response.
 *
 * <p>Shared by {@link RegisterUserUseCase} and {@link LoginUseCase}. Pure Java + domain types — no
 * framework dependencies.
 *
 * @param token the issued (e.g. JWT) authentication token
 * @param userId the authenticated user's identity
 * @param email the authenticated user's login email
 */
public record AuthResult(String token, UserId userId, String email) {}
