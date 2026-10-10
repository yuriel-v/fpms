package dev.yuriel.fpms.application.port.out;

import dev.yuriel.fpms.domain.user.UserId;

/**
 * Outbound port abstracting token issuance and validation — the application
 * issues and verifies tokens without knowing the token format (e.g. JWT).
 *
 * <p>The driven (security) adapter implements this. Pure Java + domain types —
 * no framework dependencies.
 */
public interface TokenService {

    /**
     * Issues a signed token for the given user.
     *
     * @param userId the subject the token represents
     * @return a signed token string
     */
    String issueToken(UserId userId);

    /**
     * Validates a token's signature and expiry and extracts its subject.
     *
     * @param token the token to validate
     * @return the subject {@link UserId} carried by the token
     * @throws InvalidTokenException if the token is malformed, has a bad
     *                               signature, or has expired
     */
    UserId validateAndExtractUserId(String token);
}
