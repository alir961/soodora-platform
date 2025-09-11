package com.soodora.domain.product;

public record UpdateProductCommand(String name, String description, String category, long price,
        long quantity) {
}
