package com.soodora.infrastructure.common;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class MoneyEmbeddable implements DomainEntityAdapter<Money> {

    private BigDecimal amount;

    private Currency currency;

    public MoneyEmbeddable(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public Money aggregate() {
        return Money.of(amount, currency);
    }

}
