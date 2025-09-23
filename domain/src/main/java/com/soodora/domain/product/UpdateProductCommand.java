package com.soodora.domain.product;

import com.soodora.domain.common.Currency;

public record UpdateProductCommand(String name, String description, String category, double price, Currency currency,
        long quantity) {
}
