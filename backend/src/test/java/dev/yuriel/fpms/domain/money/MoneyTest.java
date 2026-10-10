package dev.yuriel.fpms.domain.money;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Creating money from minor units and an ISO code retains both")
    void whenCreatingMoneyFromMinorUnitsAndIsoCode_thenRetainsBoth() {
        Money m = Money.of(1050, "USD");
        assertEquals(1050, m.amountMinor());
        assertEquals(Currency.getInstance("USD"), m.currency());
    }

    @Test
    @DisplayName("Creating money from a Currency instance keeps that instance")
    void whenCreatingMoneyFromCurrencyInstance_thenKeepsThatInstance() {
        Currency brl = Currency.getInstance("BRL");
        Money m = Money.of(999, brl);
        assertSame(brl, m.currency());
    }

    @Test
    @DisplayName("Creating money with a null currency is rejected")
    void whenCreatingMoneyWithNullCurrency_thenThrows() {
        assertThrows(NullPointerException.class, () -> Money.of(100, (Currency) null));
    }

    @Test
    @DisplayName("Creating money with a null currency code is rejected")
    void whenCreatingMoneyWithNullCurrencyCode_thenThrows() {
        assertThrows(NullPointerException.class, () -> Money.of(100, (String) null));
    }

    @Test
    @DisplayName("Creating money with an unknown currency code is rejected")
    void whenCreatingMoneyWithUnknownCurrencyCode_thenThrows() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(100, "XYZ"));
    }

    @Test
    @DisplayName("Creating money allows zero and negative amounts")
    void whenCreatingMoney_thenAllowsZeroAndNegativeAmounts() {
        assertEquals(0, Money.of(0, "USD").amountMinor());
        assertEquals(-250, Money.of(-250, "USD").amountMinor());
    }

    // --- zero helper ---------------------------------------------------------

    @Test
    @DisplayName("Zero money has no amount in the given currency")
    void whenCreatingZeroMoney_thenHasNoAmountInGivenCurrency() {
        Money zero = Money.zero("EUR");
        assertEquals(0, zero.amountMinor());
        assertEquals(Currency.getInstance("EUR"), zero.currency());
        assertTrue(zero.isZero());
    }

    // --- arithmetic ----------------------------------------------------------

    @Test
    @DisplayName("Adding amounts of the same currency sums them")
    void whenAddingSameCurrency_thenSumsAmounts() {
        Money result = Money.of(1000, "USD").plus(Money.of(250, "USD"));
        assertEquals(Money.of(1250, "USD"), result);
    }

    @Test
    @DisplayName("Subtracting amounts of the same currency subtracts them")
    void whenSubtractingSameCurrency_thenSubtractsAmounts() {
        Money result = Money.of(1000, "USD").minus(Money.of(250, "USD"));
        assertEquals(Money.of(750, "USD"), result);
    }

    @Test
    @DisplayName("Negating an amount flips its sign")
    void whenNegatingAmount_thenFlipsSign() {
        assertEquals(Money.of(-500, "USD"), Money.of(500, "USD").negate());
    }

    @Test
    @DisplayName("Adding different currencies is rejected")
    void whenAddingDifferentCurrencies_thenThrows() {
        Money usd = Money.of(1000, "USD");
        Money eur = Money.of(1000, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.plus(eur));
    }

    @Test
    @DisplayName("Subtracting different currencies is rejected")
    void whenSubtractingDifferentCurrencies_thenThrows() {
        Money usd = Money.of(1000, "USD");
        Money eur = Money.of(1000, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.minus(eur));
    }

    // --- equality ------------------------------------------------------------

    @Test
    @DisplayName("Money is equal when amount and currency match")
    void whenAmountAndCurrencyMatch_thenMoneyIsEqual() {
        assertEquals(Money.of(500, "USD"), Money.of(500, "USD"));
        assertEquals(Money.of(500, "USD").hashCode(), Money.of(500, "USD").hashCode());
    }

    @Test
    @DisplayName("Money is not equal when the currency differs")
    void whenCurrencyDiffers_thenMoneyIsNotEqual() {
        assertNotEquals(Money.of(500, "USD"), Money.of(500, "EUR"));
    }

    @Test
    @DisplayName("Money is not equal when the amount differs")
    void whenAmountDiffers_thenMoneyIsNotEqual() {
        assertNotEquals(Money.of(500, "USD"), Money.of(501, "USD"));
    }
}
