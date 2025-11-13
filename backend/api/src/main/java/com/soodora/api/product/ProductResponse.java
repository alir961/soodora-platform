package com.soodora.api.product;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.product.ProductDTO;

public record ProductResponse(ProductDTO productDTO) implements JsonResponse {

}
