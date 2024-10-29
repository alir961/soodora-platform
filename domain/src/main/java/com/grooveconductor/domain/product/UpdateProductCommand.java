package com.grooveconductor.domain.product;

public record UpdateProductCommand(String name, String description, String imageUrl, String category, long price, long quantity) {
}
