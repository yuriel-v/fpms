package dev.yuriel.fpms.adapter.out.security;

import dev.yuriel.fpms.application.port.out.InvalidTokenException;
import dev.yuriel.fpms.application.port.out.TokenService;
import dev.yuriel.fpms.domain.user.UserId;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Driven (security) adapter implementing the outbound {@link TokenService} port with JSON Web
 * Tokens (JJWT 0.12.x).
 *
 * <p>Tokens are HMAC-SHA signed. The signing secret is read from the {@code fpms.jwt.secret}
 * property, which is sourced from the {@code JWT_SECRET} environment variable. The secret MUST be
 * at least 32 characters (bytes) long so the derived HMAC key meets the minimum size for HS256; a
 * shorter secret fails fast at construction time.
 */
@Component
public class JwtTokenService implements TokenService {

  /** Minimum secret length (bytes) required for an HS256 HMAC key. */
  private static final int MIN_SECRET_BYTES = 32;

  private final SecretKey key;
  private final Duration expiration;

  public JwtTokenService(
      @Value("${fpms.jwt.secret}") String secret,
      @Value("${fpms.jwt.expiration-minutes:1440}") long expirationMinutes) {
    byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < MIN_SECRET_BYTES) {
      throw new IllegalStateException(
          "fpms.jwt.secret (JWT_SECRET) must be at least "
              + MIN_SECRET_BYTES
              + " characters long for HS256 signing");
    }
    this.key = Keys.hmacShaKeyFor(secretBytes);
    this.expiration = Duration.ofMinutes(expirationMinutes);
  }

  @Override
  public String issueToken(UserId userId) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId.value().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expiration)))
        .signWith(key)
        .compact();
  }

  @Override
  public UserId validateAndExtractUserId(String token) {
    try {
      String subject =
          Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
      return UserId.of(subject);
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidTokenException("token is invalid, malformed, or expired", e);
    }
  }
}
