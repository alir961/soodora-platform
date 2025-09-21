package com.soodora.domain.product;

public record CreateProductCommand(String name, String description, String category, long price,
        long quantity) {
}
