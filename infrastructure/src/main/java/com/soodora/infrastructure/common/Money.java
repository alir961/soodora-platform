package com.soodora.infrastructure.common;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.MoneyDTO;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Money {

    private BigDecimal amount;
    private Currency currency;

    protected Money() {
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
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

    public static Money zero() {
        return new Money(BigDecimal.ZERO, Currency.UNDEFINED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Money money))
            return false;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    public MoneyDTO toDTO() {
        return new MoneyDTO(this.amount, this.currency, this.currency.getSymbol());
    }

}
