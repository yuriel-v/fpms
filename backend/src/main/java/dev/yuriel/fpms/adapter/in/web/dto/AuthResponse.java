package dev.yuriel.fpms.adapter.in.web.dto;

/**
 * Inbound web DTO returned on successful registration or login.
 *
 * @param token  the issued authentication token
 * @param userId the authenticated user's id (string form of the UUID)
 * @param email  the authenticated user's login email
 */
public record AuthResponse(String token, String userId, String email) {
}
