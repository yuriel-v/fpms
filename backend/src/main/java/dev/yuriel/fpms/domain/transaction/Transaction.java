package dev.yuriel.fpms.domain.transaction;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.money.Money;
import dev.yuriel.fpms.domain.user.UserId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A one-time movement of money in or out, owned by a user.
 *
 * <p>Holds the amount as {@link Money} (minor units + currency), descriptive metadata (name, where
 * the money came {@code from} and went {@code to}), the instant it occurred, and an optional {@link
 * CategoryId} classification.
 *
 * <p>Immutable value. Pure Java — no framework dependencies.
 */
public record Transaction(
    TransactionId id,
    UserId owner,
    String name,
    TransactionType type,
    Money amount,
    String source,
    String destination,
    Instant occurredAt,
    Optional<CategoryId> categoryId) {

  public Transaction {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(type, "type must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    Objects.requireNonNull(categoryId, "categoryId optional must not be null");
    name = requireNonBlank(name, "name");
    source = requireNonBlank(source, "source");
    destination = requireNonBlank(destination, "destination");
  }

  /** Reconstitute a transaction with a known id; {@code categoryId} may be null. */
  public static Transaction create(
      TransactionId id,
      UserId owner,
      String name,
      TransactionType type,
      Money amount,
      String source,
      String destination,
      Instant occurredAt,
      CategoryId categoryId) {
    return new Transaction(
        id,
        owner,
        name,
        type,
        amount,
        source,
        destination,
        occurredAt,
        Optional.ofNullable(categoryId));
  }

  /** Create a brand-new transaction with a freshly generated id. */
  public static Transaction newTransaction(
      UserId owner,
      String name,
      TransactionType type,
      Money amount,
      String source,
      String destination,
      Instant occurredAt,
      CategoryId categoryId) {
    return create(
        TransactionId.newId(),
        owner,
        name,
        type,
        amount,
        source,
        destination,
        occurredAt,
        categoryId);
  }

  /**
   * The amount with a sign reflecting its direction: positive for income, negative for expense.
   * Useful for computing per-currency net totals.
   */
  public Money signedAmount() {
    return type == TransactionType.EXPENSE ? amount.negate() : amount;
  }

  private static String requireNonBlank(String raw, String field) {
    Objects.requireNonNull(raw, field + " must not be null");
    String trimmed = raw.strip();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return trimmed;
  }
}
