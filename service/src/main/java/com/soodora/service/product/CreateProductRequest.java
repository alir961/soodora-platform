package com.soodora.service.product;

import com.soodora.domain.common.Currency;
import com.soodora.domain.product.CreateProductCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateProduct;

public record CreateProductRequest(
        @Schema(description = "Product name", example = "My Product") String name,
        @Schema(description = "Product description", example = "My New Product") String description,
        @Schema(description = "Product category", example = "CD") String category,
        @Schema(description = "Product price", example = "49.99") Double price,
        @Schema(description = "Product currency", example = "USD", allowableValues = {
                "USD", "EUR", "CAD", "GBP"}) Currency currency,
        @Schema(description = "Product quantity", example = "100") long quantity)
        implements
            Request<CreateProductCommand>{

    @Override
    public CreateProductCommand toCommand() {
        validateProduct(name, description, category, price, currency, quantity);

        return new CreateProductCommand(name.trim(), description.trim(), category, price, currency, quantity);
    }

}
