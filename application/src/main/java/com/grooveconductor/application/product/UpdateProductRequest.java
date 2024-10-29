package com.grooveconductor.application.product;

import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.product.UpdateProductCommand;

public record UpdateProductRequest(String name, String description, String imageUrl, String category, long price, long quantity) implements Request<UpdateProductCommand> {

    @Override
    public UpdateProductCommand toCommand() {
        if (name == null) throw new InvalidRequestException("Name is not valid");
        if (description == null) throw new InvalidRequestException("Description is not valid");
        if (price == 0) throw new InvalidRequestException("Price is not valid");
        if (quantity == 0) throw new InvalidRequestException("Quantity is not valid");

        return new UpdateProductCommand(name, description, imageUrl, category, price, quantity);
    }

}
