package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.service.common.Request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateQuoteArticle;

public record CreateQuoteArticleRequest(
        @Schema(description = "Quote slug", example = "quoteSlug") String quoteSlug,
        @Schema(description = "Quote Article description", example = "My Article") String description,
        @Schema(description = "Quote Article quantity", example = "3") int quantity,
        @Schema(description = "Quote Article price", example = "49.99") Double price,
        @Schema(description = "Quote Article currency", example = "USD", allowableValues = {
                "USD", "EUR", "CAD", "GBP"}) Currency currency,
        @Schema(description = "Quote Article TAX Percent", example = "20%") BigDecimal vatPercent)
        implements
            Request<CreateQuoteArticleCommand>{

    @Override
    public CreateQuoteArticleCommand toCommand() {
        validateQuoteArticle(quoteSlug, description, quantity, price, currency, vatPercent);

        return new CreateQuoteArticleCommand(quoteSlug, description.trim(), quantity, price, currency, vatPercent);
    }

}
