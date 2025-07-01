package com.soodora.domain.product;

import lombok.Getter;

@Getter
public class UpdateProductCommand {
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String category;
    private final long price;
    private final long quantity;

    public UpdateProductCommand(
            String name,
            String description,
            String imageUrl,
            String category,
            Long price,
            Long quantity) {

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

}
