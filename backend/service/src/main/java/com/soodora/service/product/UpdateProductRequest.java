package com.soodora.service.product;

import com.soodora.domain.common.Currency;
import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.*;

public record UpdateProductRequest(
        @Schema(description = "Product name", example = "My Product") String name,
        @Schema(description = "Product description", example = "My New Product") String description,
        @Schema(description = "Product category", example = "CD") String category,
        @Schema(description = "Product price", example = "49.99") BigDecimal price,
        @Schema(description = "Product currency", example = "USD", allowableValues = {
                "USD", "EUR", "CAD", "GBP"}) Currency currency,
        @Schema(description = "Product quantity", example = "100") long quantity)
        implements
            Request<UpdateProductCommand>{

    @Override
    public UpdateProductCommand toCommand() {
        if (!isStringValid(name, 255, true))
            throw new InvalidRequestException("Name cannot exceed 255 characters");
        if (!isStringValid(description, 255, true))
            throw new InvalidRequestException("Description cannot exceed 255 characters");
        if (!isStringValid(category, 50, true))
            throw new InvalidRequestException("Category cannot exceed 50 characters");
        if (!isValidMoneyPrice(price))
            throw new InvalidRequestException(
                    "Price must be greater than 0, cannot exceed 100,000 and must have only two digits cents");
        if (!isValidCurrency(currency))
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
