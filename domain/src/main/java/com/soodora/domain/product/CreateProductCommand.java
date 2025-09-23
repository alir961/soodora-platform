package com.soodora.domain.product;

import com.soodora.domain.common.Currency;

public record CreateProductCommand(String name, String description, String category, double price, Currency currency,
        long quantity) {
}
