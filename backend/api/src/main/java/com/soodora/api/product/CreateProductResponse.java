package com.soodora.api.product;

import com.soodora.api.common.JsonResponse;

public record CreateProductResponse(String slug) implements JsonResponse {

}
