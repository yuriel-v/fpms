package dev.yuriel.fpms.domain.user;

/**
 * Canonical normalization for user email addresses: trim surrounding whitespace and lowercase.
 * Centralizes the one form the system treats as equal so lookups and duplicate checks agree with
 * how {@link User} stores an email.
 *
 * <p>Deliberately narrow — this is not a general-purpose string utility. Pure Java; may later be
 * subsumed by an {@code Email} value object.
 */
public final class EmailNormalizer {

  private EmailNormalizer() {}

  /**
   * Returns the normalized (trimmed, lowercased) form of {@code email}, or {@code null} if {@code
   * email} is {@code null}.
   */
  public static String normalize(String email) {
    return email == null ? null : email.strip().toLowerCase();
  }
}
