package dev.yuriel.fpms.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of a {@code User}, wrapping a {@link UUID} to keep the domain explicit and type-safe.
 * Pure Java — no framework dependencies.
 */
public record UserId(UUID value) {

  public UserId {
    Objects.requireNonNull(value, "UserId value must not be null");
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId of(UUID value) {
    return new UserId(value);
  }

  public static UserId of(String value) {
    return new UserId(UUID.fromString(value));
  }
}
