package com.grooveconductor.domain.command;

public record CreateProductCommand(String name, String description, String imageUrl, String category, long price, long quantity) {
}
