package com.soodora.domain.common;

import java.math.BigDecimal;

import org.springframework.lang.NonNull;

import static java.math.BigDecimal.ZERO;

import static com.soodora.domain.common.Currency.UNDEFINED;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null || currency == null)
            throw new IllegalArgumentException("Amount and currency must not be null");
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero() {
        return new Money(ZERO, UNDEFINED);
    }

    public Money add(Money other) {
        assertCurrencyMatching(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        assertCurrencyMatching(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(long factor) {
        BigDecimal decimalFactor = BigDecimal.valueOf(factor);

        return new Money(amount.multiply(decimalFactor), currency);
    }

    public String getSymbol() {
        return currency.getSymbol();
    }

    @NonNull
    @Override
    public String toString() {
        return amount + " " + currency + " " + getSymbol();
    }

    private void assertCurrencyMatching(Money other) {
        if (currency.equals(other.currency))
            return;
        throw new CurrenciesMatchException("Currencies must match");
    }

}
