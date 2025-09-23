package com.soodora.domain.common;

import java.math.BigDecimal;

public record MoneyDTO(BigDecimal amount, Currency currency, String symbol) {
}
