package dev.yuriel.fpms.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.money.Money;
import dev.yuriel.fpms.domain.user.UserId;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TransactionTest {

  private static final TransactionId ID = TransactionId.newId();
  private static final UserId OWNER = UserId.newId();
  private static final Money AMOUNT = Money.of(1500, "USD");
  private static final Instant WHEN = Instant.parse("2026-03-15T10:00:00Z");
  private static final CategoryId CATEGORY = CategoryId.newId();

  @Test
  @DisplayName("Creating a fully specified transaction with a category retains all fields")
  void whenCreatingFullySpecifiedTransactionWithCategoryThenRetainsAllFields() {
    Transaction t =
        Transaction.create(
            ID,
            OWNER,
            "March salary",
            TransactionType.INCOME,
            AMOUNT,
            "Employer",
            "Checking account",
            WHEN,
            CATEGORY);

    assertEquals(ID, t.id());
    assertEquals(OWNER, t.owner());
    assertEquals("March salary", t.name());
    assertEquals(TransactionType.INCOME, t.type());
    assertEquals(AMOUNT, t.amount());
    assertEquals("Employer", t.source());
    assertEquals("Checking account", t.destination());
    assertEquals(WHEN, t.occurredAt());
    assertEquals(Optional.of(CATEGORY), t.categoryId());
  }

  @Test
  @DisplayName("Creating a transaction with a null category leaves the category absent")
  void whenCreatingTransactionWithNullCategoryThenCategoryIsAbsent() {
    Transaction t =
        Transaction.create(
            ID,
            OWNER,
            "Coffee",
            TransactionType.EXPENSE,
            Money.of(450, "USD"),
            "Wallet",
            "Cafe",
            WHEN,
            null);
    assertTrue(t.categoryId().isEmpty());
  }

  @Test
  @DisplayName("Creating a new transaction generates an id")
  void whenCreatingNewTransactionThenGeneratesAnId() {
    Transaction t =
        Transaction.newTransaction(
            OWNER,
            "Coffee",
            TransactionType.EXPENSE,
            Money.of(450, "USD"),
            "Wallet",
            "Cafe",
            WHEN,
            null);
    assertNotNull(t.id());
  }

  @Test
  @DisplayName("Creating a transaction trims the name, source and destination")
  void whenCreatingTransactionThenTrimsNameSourceAndDestination() {
    Transaction t =
        Transaction.create(
            ID,
            OWNER,
            "  Rent  ",
            TransactionType.EXPENSE,
            AMOUNT,
            "  Checking  ",
            "  Landlord  ",
            WHEN,
            null);
    assertEquals("Rent", t.name());
    assertEquals("Checking", t.source());
    assertEquals("Landlord", t.destination());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("creationsWithANullRequiredField")
  @DisplayName("Creating a transaction with any null required field is rejected")
  void whenCreatingTransactionWithNullRequiredFieldThenThrows(String field, Executable creation) {
    assertThrows(NullPointerException.class, creation);
  }

  private static Stream<Arguments> creationsWithANullRequiredField() {
    return Stream.of(
        Arguments.of(
            "null id",
            (Executable)
                () ->
                    Transaction.create(
                        null, OWNER, "n", TransactionType.INCOME, AMOUNT, "f", "t", WHEN, null)),
        Arguments.of(
            "null owner",
            (Executable)
                () ->
                    Transaction.create(
                        ID, null, "n", TransactionType.INCOME, AMOUNT, "f", "t", WHEN, null)),
        Arguments.of(
            "null type",
            (Executable)
                () -> Transaction.create(ID, OWNER, "n", null, AMOUNT, "f", "t", WHEN, null)),
        Arguments.of(
            "null amount",
            (Executable)
                () ->
                    Transaction.create(
                        ID, OWNER, "n", TransactionType.INCOME, null, "f", "t", WHEN, null)),
        Arguments.of(
            "null occurredAt",
            (Executable)
                () ->
                    Transaction.create(
                        ID, OWNER, "n", TransactionType.INCOME, AMOUNT, "f", "t", null, null)));
  }

  @ParameterizedTest(name = "blank {0}")
  @MethodSource("creationsWithABlankTextField")
  @DisplayName("Creating a transaction with a blank name, source or destination is rejected")
  void whenCreatingTransactionWithBlankTextFieldThenThrows(String field, Executable creation) {
    assertThrows(IllegalArgumentException.class, creation);
  }

  private static Stream<Arguments> creationsWithABlankTextField() {
    return Stream.of(
        Arguments.of(
            "name",
            (Executable)
                () ->
                    Transaction.create(
                        ID, OWNER, "  ", TransactionType.INCOME, AMOUNT, "f", "t", WHEN, null)),
        Arguments.of(
            "source",
            (Executable)
                () ->
                    Transaction.create(
                        ID, OWNER, "n", TransactionType.INCOME, AMOUNT, "  ", "t", WHEN, null)),
        Arguments.of(
            "destination",
            (Executable)
                () ->
                    Transaction.create(
                        ID, OWNER, "n", TransactionType.INCOME, AMOUNT, "f", "  ", WHEN, null)));
  }

  @Test
  @DisplayName("The signed amount is negative for an expense and positive for income")
  void whenComputingSignedAmountThenNegativeForExpenseAndPositiveForIncome() {
    Transaction income =
        Transaction.create(
            ID, OWNER, "pay", TransactionType.INCOME, Money.of(1000, "USD"), "f", "t", WHEN, null);
    Transaction expense =
        Transaction.create(
            TransactionId.newId(),
            OWNER,
            "buy",
            TransactionType.EXPENSE,
            Money.of(1000, "USD"),
            "f",
            "t",
            WHEN,
            null);

    assertEquals(Money.of(1000, "USD"), income.signedAmount());
    assertEquals(Money.of(-1000, "USD"), expense.signedAmount());
  }
}
