package dev.yuriel.fpms.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailNormalizerTest {

  @Test
  @DisplayName("normalize trims surrounding whitespace and lowercases the address")
  void whenGivenMixedCaseAndWhitespace_thenTrimsAndLowercases() {
    assertEquals("me@example.com", EmailNormalizer.normalize("  Me@Example.COM  "));
  }

  @Test
  @DisplayName("normalize leaves an already-normalized address unchanged")
  void whenGivenNormalizedAddress_thenReturnsItUnchanged() {
    assertEquals("me@example.com", EmailNormalizer.normalize("me@example.com"));
  }

  @Test
  @DisplayName("normalize returns null when the input is null")
  void whenGivenNull_thenReturnsNull() {
    assertNull(EmailNormalizer.normalize(null));
  }
}
