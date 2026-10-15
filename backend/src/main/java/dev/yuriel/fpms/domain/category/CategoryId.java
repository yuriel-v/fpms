package dev.yuriel.fpms.domain.category;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of a {@code Category}, wrapping a {@link UUID} to keep the domain explicit and
 * type-safe. Pure Java — no framework dependencies.
 */
public record CategoryId(UUID value) {

  public CategoryId {
    Objects.requireNonNull(value, "CategoryId value must not be null");
  }

  public static CategoryId newId() {
    return new CategoryId(UUID.randomUUID());
  }

  public static CategoryId of(UUID value) {
    return new CategoryId(value);
  }

  public static CategoryId of(String value) {
    return new CategoryId(UUID.fromString(value));
  }
}
