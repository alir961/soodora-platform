package com.grooveconductor.application.product;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.product.UpdateProductCommand;

import static java.util.Optional.ofNullable;

public record UpdateProductRequest(
    String name,
    String description,
    String imageUrl,
    String category,
    Long price,
    Long quantity
) implements Request<UpdateProductCommand> {

    @Override
    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(
            ofNullable(name),
            ofNullable(description),
            ofNullable(imageUrl),
            ofNullable(category),
            ofNullable(price),
            ofNullable(quantity));
    }

}
