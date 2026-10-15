package dev.yuriel.fpms.application.port.out;

/**
 * Outbound port abstracting password hashing — the application hashes and verifies passwords
 * without knowing the algorithm.
 *
 * <p>The driven (security) adapter implements this, e.g. with BCrypt. Pure Java — no framework
 * dependencies.
 */
public interface PasswordHasher {

  /**
   * Hashes a raw password for storage.
   *
   * @param rawPassword the plaintext password
   * @return the hashed representation to persist
   */
  String hash(String rawPassword);

  /**
   * Verifies a raw password against a stored hash.
   *
   * @param rawPassword the plaintext password to check
   * @param passwordHash the stored hash to check against
   * @return {@code true} if the raw password matches the hash
   */
  boolean matches(String rawPassword, String passwordHash);
}
