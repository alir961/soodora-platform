package com.grooveconductor.application.product;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.application.common.InvalidRequestException;

public record CreateProductRequest(String name, String description, String imageUrl, String category, long price, long quantity) implements Request<CreateProductCommand> {

    @Override
    public CreateProductCommand toCommand() {
        if (name == null) throw new InvalidRequestException("Name is not valid");
        if (description == null) throw new InvalidRequestException("Description is not valid");
        if (price == 0) throw new InvalidRequestException("Price is not valid");
        if (quantity == 0) throw new InvalidRequestException("Quantity is not valid");

        return new CreateProductCommand(name, description, imageUrl, category, price, quantity);
    }

}
