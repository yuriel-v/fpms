package dev.yuriel.fpms.domain.money;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    // --- construction & validation ------------------------------------------

    @Test
    void creates_money_from_minor_units_and_iso_code() {
        Money m = Money.of(1050, "USD");
        assertEquals(1050, m.amountMinor());
        assertEquals(Currency.getInstance("USD"), m.currency());
    }

    @Test
    void creates_money_from_a_currency_instance() {
        Currency brl = Currency.getInstance("BRL");
        Money m = Money.of(999, brl);
        assertSame(brl, m.currency());
    }

    @Test
    void rejects_null_currency() {
        assertThrows(NullPointerException.class, () -> Money.of(100, (Currency) null));
    }

    @Test
    void rejects_null_currency_code() {
        assertThrows(NullPointerException.class, () -> Money.of(100, (String) null));
    }

    @Test
    void rejects_unknown_currency_code() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(100, "XYZ"));
    }

    @Test
    void allows_zero_and_negative_amounts() {
        assertEquals(0, Money.of(0, "USD").amountMinor());
        assertEquals(-250, Money.of(-250, "USD").amountMinor());
    }

    // --- zero helper ---------------------------------------------------------

    @Test
    void zero_has_no_amount_in_the_given_currency() {
        Money zero = Money.zero("EUR");
        assertEquals(0, zero.amountMinor());
        assertEquals(Currency.getInstance("EUR"), zero.currency());
        assertTrue(zero.isZero());
    }

    // --- arithmetic ----------------------------------------------------------

    @Test
    void adds_amounts_of_the_same_currency() {
        Money result = Money.of(1000, "USD").plus(Money.of(250, "USD"));
        assertEquals(Money.of(1250, "USD"), result);
    }

    @Test
    void subtracts_amounts_of_the_same_currency() {
        Money result = Money.of(1000, "USD").minus(Money.of(250, "USD"));
        assertEquals(Money.of(750, "USD"), result);
    }

    @Test
    void negates_an_amount() {
        assertEquals(Money.of(-500, "USD"), Money.of(500, "USD").negate());
    }

    @Test
    void adding_different_currencies_is_rejected() {
        Money usd = Money.of(1000, "USD");
        Money eur = Money.of(1000, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.plus(eur));
    }

    @Test
    void subtracting_different_currencies_is_rejected() {
        Money usd = Money.of(1000, "USD");
        Money eur = Money.of(1000, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.minus(eur));
    }

    // --- equality ------------------------------------------------------------

    @Test
    void equal_when_same_amount_and_currency() {
        assertEquals(Money.of(500, "USD"), Money.of(500, "USD"));
        assertEquals(Money.of(500, "USD").hashCode(), Money.of(500, "USD").hashCode());
    }

    @Test
    void not_equal_when_currency_differs() {
        assertNotEquals(Money.of(500, "USD"), Money.of(500, "EUR"));
    }

    @Test
    void not_equal_when_amount_differs() {
        assertNotEquals(Money.of(500, "USD"), Money.of(501, "USD"));
    }
}
