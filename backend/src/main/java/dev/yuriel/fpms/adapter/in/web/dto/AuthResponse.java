package dev.yuriel.fpms.adapter.in.web.dto;

import dev.yuriel.fpms.application.port.in.AuthResult;

/**
 * Inbound web DTO returned on successful registration or login.
 *
 * @param token  the issued authentication token
 * @param userId the authenticated user's id (string form of the UUID)
 * @param email  the authenticated user's login email
 */
public record AuthResponse(String token, String userId, String email) {

    /**
     * Maps an application-layer {@link AuthResult} to the web response shape.
     */
    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(
                result.token(),
                result.userId().value().toString(),
                result.email());
    }
}
