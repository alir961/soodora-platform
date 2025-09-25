package com.soodora.domain.product;

import com.soodora.domain.common.Currency;

import java.math.BigDecimal;

public record CreateProductCommand(String name, String description, String category, BigDecimal price,
        Currency currency,
        long quantity) {
}
