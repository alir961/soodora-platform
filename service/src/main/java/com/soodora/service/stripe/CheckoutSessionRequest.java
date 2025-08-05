package com.soodora.service.stripe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutSessionRequest(
        @NotBlank String name,
        @NotNull @Min(1) Long amount,
        String imageUrl,
        @NotBlank String productSlug,
        @NotBlank String buyerSlug,
        @NotNull @Min(1) Long quantity) {
}
