package com.soodora.domain.common;

import lombok.Getter;

@Getter
public enum Currency {
    EUR("€"), USD("$"), CAD("C$"), GBP("£");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

}
