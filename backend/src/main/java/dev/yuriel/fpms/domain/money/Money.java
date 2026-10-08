package dev.yuriel.fpms.domain.money;

import java.util.Currency;
import java.util.Objects;

/**
 * A monetary amount expressed in integer <em>minor units</em> (e.g. cents)
 * together with its ISO 4217 {@link Currency}.
 *
 * <p>Storing amounts as integer minor units avoids floating-point rounding
 * error. This value object owns all monetary arithmetic and forbids mixing
 * currencies: {@link #plus(Money)} and {@link #minus(Money)} throw
 * {@link CurrencyMismatchException} when operands differ in currency.
 *
 * <p>Pure Java — no framework dependencies.
 */
public record Money(long amountMinor, Currency currency) {

    public Money {
        Objects.requireNonNull(currency, "currency must not be null");
    }

    public static Money of(long amountMinor, Currency currency) {
        return new Money(amountMinor, currency);
    }

    public static Money of(long amountMinor, String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        return new Money(amountMinor, Currency.getInstance(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return of(0, currencyCode);
    }

    public static Money zero(Currency currency) {
        return of(0, currency);
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(this.amountMinor + other.amountMinor, this.currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(this.amountMinor - other.amountMinor, this.currency);
    }

    public Money negate() {
        return new Money(-this.amountMinor, this.currency);
    }

    public boolean isZero() {
        return this.amountMinor == 0;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this.currency, other.currency);
        }
    }
}
