package com.soodora.service.stripe;

import com.soodora.domain.common.Currency;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutSessionRequest(
        @NotBlank String name,
        @NotNull @Min(1) Double amount,
        @NotNull Currency currency,
        String imageUrl,
        @NotBlank String productSlug,
        @NotBlank String vendorSlug,
        @NotBlank String buyerSlug,
        @NotNull @Min(1) Long quantity) {
}
