package dev.yuriel.fpms.domain.money;

import java.util.Currency;

/**
 * Thrown when an operation attempts to combine {@link Money} values of different currencies.
 * Monetary arithmetic is only defined within a single currency; totals are always reported per
 * currency.
 */
public class CurrencyMismatchException extends RuntimeException {

  public CurrencyMismatchException(Currency left, Currency right) {
    super(
        "Cannot operate on mixed currencies: "
            + left.getCurrencyCode()
            + " and "
            + right.getCurrencyCode());
  }
}
