package dev.yuriel.fpms.domain.transaction;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.money.Money;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    private final TransactionId id = TransactionId.newId();
    private final UserId owner = UserId.newId();
    private final Money amount = Money.of(1500, "USD");
    private final Instant when = Instant.parse("2026-03-15T10:00:00Z");
    private final CategoryId category = CategoryId.newId();

    @Test
    void creates_a_fully_specified_transaction_with_a_category() {
        Transaction t = Transaction.create(
                id, owner, "March salary", TransactionType.INCOME,
                amount, "Employer", "Checking account", when, category);

        assertEquals(id, t.id());
        assertEquals(owner, t.owner());
        assertEquals("March salary", t.name());
        assertEquals(TransactionType.INCOME, t.type());
        assertEquals(amount, t.amount());
        assertEquals("Employer", t.source());
        assertEquals("Checking account", t.destination());
        assertEquals(when, t.occurredAt());
        assertEquals(Optional.of(category), t.categoryId());
    }

    @Test
    void category_is_optional_and_absent_when_null() {
        Transaction t = Transaction.create(
                id, owner, "Coffee", TransactionType.EXPENSE,
                Money.of(450, "USD"), "Wallet", "Cafe", when, null);
        assertTrue(t.categoryId().isEmpty());
    }

    @Test
    void new_transaction_generates_an_id() {
        Transaction t = Transaction.newTransaction(
                owner, "Coffee", TransactionType.EXPENSE,
                Money.of(450, "USD"), "Wallet", "Cafe", when, null);
        assertNotNull(t.id());
    }

    @Test
    void trims_name_source_and_destination() {
        Transaction t = Transaction.create(
                id, owner, "  Rent  ", TransactionType.EXPENSE,
                amount, "  Checking  ", "  Landlord  ", when, null);
        assertEquals("Rent", t.name());
        assertEquals("Checking", t.source());
        assertEquals("Landlord", t.destination());
    }

    @Test
    void rejects_null_required_fields() {
        assertThrows(NullPointerException.class, () -> Transaction.create(
                null, owner, "n", TransactionType.INCOME, amount, "f", "t", when, null));
        assertThrows(NullPointerException.class, () -> Transaction.create(
                id, null, "n", TransactionType.INCOME, amount, "f", "t", when, null));
        assertThrows(NullPointerException.class, () -> Transaction.create(
                id, owner, "n", null, amount, "f", "t", when, null));
        assertThrows(NullPointerException.class, () -> Transaction.create(
                id, owner, "n", TransactionType.INCOME, null, "f", "t", when, null));
        assertThrows(NullPointerException.class, () -> Transaction.create(
                id, owner, "n", TransactionType.INCOME, amount, "f", "t", null, null));
    }

    @Test
    void rejects_blank_name_source_or_destination() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.create(
                id, owner, "  ", TransactionType.INCOME, amount, "f", "t", when, null));
        assertThrows(IllegalArgumentException.class, () -> Transaction.create(
                id, owner, "n", TransactionType.INCOME, amount, "  ", "t", when, null));
        assertThrows(IllegalArgumentException.class, () -> Transaction.create(
                id, owner, "n", TransactionType.INCOME, amount, "f", "  ", when, null));
    }

    @Test
    void signed_amount_is_negative_for_expense_and_positive_for_income() {
        Transaction income = Transaction.create(
                id, owner, "pay", TransactionType.INCOME,
                Money.of(1000, "USD"), "f", "t", when, null);
        Transaction expense = Transaction.create(
                TransactionId.newId(), owner, "buy", TransactionType.EXPENSE,
                Money.of(1000, "USD"), "f", "t", when, null);

        assertEquals(Money.of(1000, "USD"), income.signedAmount());
        assertEquals(Money.of(-1000, "USD"), expense.signedAmount());
    }
}
