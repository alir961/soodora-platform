package com.soodora.service.product;

import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.service.common.Request;

public record UpdateProductRequest(
        String name,
        String description,
        String category,
        Double price,
        Currency currency,
        Long quantity) implements Request<UpdateProductCommand> {

    @Override
    public UpdateProductCommand toCommand() {
        if (!isStringValid(name, 255))
            throw new InvalidRequestException("Name cannot exceed 255 characters");
        if (!isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters");
        if (!isStringValid(category, 50))
            throw new InvalidRequestException("Category cannot exceed 50 characters");
        if (!isLongInRange(price, 100000))
            throw new InvalidRequestException("Price cannot exceed 100 000 or be 0");
        if (!isLongInRange(quantity, 100000))
            throw new InvalidRequestException("Quantity cannot exceed 100 000 or be 0");

        return new UpdateProductCommand(
                name,
                description,
                category,
                price,
                currency,
                quantity);
    }

}
