package com.grooveconductor.rest.product;

import com.grooveconductor.rest.common.JsonResponse;

public record CreateProductResponse(String slug) implements JsonResponse {

}
