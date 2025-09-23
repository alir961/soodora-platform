package com.soodora.service.product;

import com.soodora.domain.product.CreateProductCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateProduct;

public record CreateProductRequest(
        @Schema(description = "Product name", example = "My Product") String name,
        @Schema(description = "Product description", example = "There is my new drop !") String description,
        @Schema(description = "Product category", example = "MERCH") String category,
        @Schema(description = "Product price", example = "35") long price,
        @Schema(description = "Product quantity", example = "222") long quantity)
        implements
            Request<CreateProductCommand> {

    @Override
    public CreateProductCommand toCommand() {
        validateProduct(name, category, price, quantity);

        return new CreateProductCommand(name.trim(), description.trim(), category, price, quantity);
    }

}
