package com.soodora.domain.common;

import java.math.BigDecimal;

import org.springframework.lang.NonNull;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("Amount and currency must not be null");
        }
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, Currency.UNDEFINED);
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    private void checkCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrenciesMatchException("Currencies must match");
        }
    }

    public String getSymbol() {
        return currency.getSymbol();
    }

    @Override
    @NonNull
    public String toString() {
        return amount + " " + currency + " " + getSymbol();
    }

}
