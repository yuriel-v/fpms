package dev.yuriel.fpms.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.yuriel.fpms.application.port.out.InvalidTokenException;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Fast, context-free tests for {@link JwtTokenService}: construct it directly with a >= 32-char
 * test secret and exercise the issue/validate round trip.
 */
class JwtTokenServiceTest {

  private static final String TEST_SECRET = "test-secret-that-is-at-least-32-bytes-long!!";

  private final JwtTokenService service = new JwtTokenService(TEST_SECRET, 60);

  @Test
  @DisplayName("A freshly issued token validates back to the same user id")
  void whenValidatingFreshlyIssuedTokenThenReturnsSameUserId() {
    UserId userId = UserId.newId();

    String token = service.issueToken(userId);
    UserId extracted = service.validateAndExtractUserId(token);

    assertThat(extracted).isEqualTo(userId);
  }

  @Test
  @DisplayName("A tampered token is rejected")
  void whenValidatingTamperedTokenThenThrowsInvalidToken() {
    UserId userId = UserId.newId();
    String token = service.issueToken(userId);
    String tampered = token.substring(0, token.length() - 2) + (token.endsWith("a") ? "bb" : "aa");

    assertThatThrownBy(() -> service.validateAndExtractUserId(tampered))
        .isInstanceOf(InvalidTokenException.class);
  }

  @Test
  @DisplayName("A garbage (non-JWT) token is rejected")
  void whenValidatingGarbageTokenThenThrowsInvalidToken() {
    assertThatThrownBy(() -> service.validateAndExtractUserId("not-a-jwt"))
        .isInstanceOf(InvalidTokenException.class);
  }

  @Test
  @DisplayName("A token signed with a different secret is rejected")
  void whenValidatingTokenSignedWithDifferentSecretThenThrowsInvalidToken() {
    UserId userId = UserId.newId();
    String token = service.issueToken(userId);

    JwtTokenService otherService =
        new JwtTokenService("another-secret-that-is-also-32-bytes-long!!", 60);

    assertThatThrownBy(() -> otherService.validateAndExtractUserId(token))
        .isInstanceOf(InvalidTokenException.class);
  }

  @Test
  @DisplayName("Constructing the service with a too-short secret fails fast")
  void whenConstructingWithShortSecretThenFailsFast() {
    assertThatThrownBy(() -> new JwtTokenService("too-short", 60))
        .isInstanceOf(IllegalStateException.class);
  }
}
