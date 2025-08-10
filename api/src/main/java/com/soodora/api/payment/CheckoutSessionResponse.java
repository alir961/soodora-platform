package com.soodora.api.payment;

import com.soodora.api.common.JsonResponse;

public record CheckoutSessionResponse(String url) implements JsonResponse {
}
