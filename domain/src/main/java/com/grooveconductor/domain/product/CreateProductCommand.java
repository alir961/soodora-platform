package com.grooveconductor.domain.product;

public record CreateProductCommand(String name, String description, String imageUrl, String category, long price,
                                   long quantity) {
}
