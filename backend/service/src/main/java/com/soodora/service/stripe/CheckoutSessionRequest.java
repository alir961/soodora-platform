package com.soodora.service.stripe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Only identifies what to buy. Price, name, image and vendor are read from the
 * stored product and the buyer is the authenticated user, so a client cannot
 * change what it pays.
 */
public record CheckoutSessionRequest(@NotBlank String productSlug, @NotNull @Min(1) Long quantity) {
}
