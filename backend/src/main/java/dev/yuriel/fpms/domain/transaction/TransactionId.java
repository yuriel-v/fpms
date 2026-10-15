package dev.yuriel.fpms.domain.transaction;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity of a {@code Transaction}, wrapping a {@link UUID} to keep the domain explicit and
 * type-safe. Pure Java — no framework dependencies.
 */
public record TransactionId(UUID value) {

  public TransactionId {
    Objects.requireNonNull(value, "TransactionId value must not be null");
  }

  public static TransactionId newId() {
    return new TransactionId(UUID.randomUUID());
  }

  public static TransactionId of(UUID value) {
    return new TransactionId(value);
  }

  public static TransactionId of(String value) {
    return new TransactionId(UUID.fromString(value));
  }
}
