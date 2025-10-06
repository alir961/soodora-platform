package com.soodora.infrastructure.common;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable
public class MoneyEmbeddable {

    private BigDecimal amount;

    private Currency currency;

    protected MoneyEmbeddable() {
    }

    public MoneyEmbeddable(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Money toDomain() {
        return Money.of(amount, currency);
    }

}
