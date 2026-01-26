package com.soodora.api.quote;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

public record QuoteArticleResponse(QuoteArticleDTO quoteArticleDTO) implements JsonResponse {

}
