package com.grooveconductor.service.product;

import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

public record CreateProductRequest(
        String name,
        String description,
        String imageUrl,
        String category,
        long price,
        long quantity) implements Request<CreateProductCommand> {

    @Override
    public CreateProductCommand toCommand() {
        if (name == null || name.isEmpty())
            throw new InvalidRequestException("Name is not valid");
        if (description == null || description.isEmpty())
            throw new InvalidRequestException("Description is not valid");
        if (price == 0)
            throw new InvalidRequestException("Price is not valid");
        if (quantity == 0)
            throw new InvalidRequestException("Quantity is not valid");

        return new CreateProductCommand(name.trim(), description.trim(), imageUrl, category, price, quantity);
    }

}
