package com.grooveconductor.api.product;

import com.grooveconductor.api.common.JsonResponse;

public record CreateProductResponse(String slug) implements JsonResponse {

}
