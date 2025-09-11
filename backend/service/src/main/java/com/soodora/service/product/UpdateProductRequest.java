package com.soodora.service.product;

import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.service.common.Request;

public record UpdateProductRequest(
        String name,
        String description,
        String category,
        Long price,
        Long quantity) implements Request<UpdateProductCommand> {

    @Override
    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(
                name,
                description,
                category,
                price,
                quantity);
    }

}
