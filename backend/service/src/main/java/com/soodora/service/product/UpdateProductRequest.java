package com.soodora.service.product;

import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.*;

public record UpdateProductRequest(
        @Schema(description = "Product name", example = "My Product") String name,
        @Schema(description = "Product description", example = "There is my new drop !") String description,
        @Schema(description = "Product category", example = "MERCH") String category,
        @Schema(description = "Product price", example = "35") long price,
        @Schema(description = "Product quantity", example = "222") long quantity)
        implements
            Request<UpdateProductCommand> {

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

        return new UpdateProductCommand(name.trim(), description.trim(), category, price, quantity);
    }

}
