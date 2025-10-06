package com.soodora.api.quote;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quote.QuoteDTO;

public record QuoteResponse(QuoteDTO quoteDTO) implements JsonResponse {

}
