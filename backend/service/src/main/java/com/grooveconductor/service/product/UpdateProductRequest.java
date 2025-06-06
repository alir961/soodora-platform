package com.grooveconductor.service.product;

import com.grooveconductor.domain.product.UpdateProductCommand;
import com.grooveconductor.service.common.Request;

public record UpdateProductRequest(
        String name,
        String description,
        String imageUrl,
        String category,
        Long price,
        Long quantity) implements Request<UpdateProductCommand> {

    @Override
    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(
                name,
                description,
                imageUrl,
                category,
                price,
                quantity);
    }

}
