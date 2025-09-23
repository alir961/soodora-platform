package com.soodora.service.product;

import com.soodora.domain.product.CreateProductCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CreateProductRequest(
        String name,
        String description,
        String category,
        Double price,
        Currency currency,
        long quantity) implements Request<CreateProductCommand> {

    @Override
    public CreateProductCommand toCommand() {
        validateProduct(name, category, price, quantity);

        return new CreateProductCommand(name.trim(), description.trim(), category, price, quantity);
    }

}
