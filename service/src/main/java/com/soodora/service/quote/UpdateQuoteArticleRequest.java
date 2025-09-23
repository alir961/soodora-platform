package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.*;

public record UpdateQuoteArticleRequest(
        @Schema(description = "Quote slug", example = "quoteSlug") String quoteSlug,
        @Schema(description = "Quote Article description", example = "My Article") String description,
        @Schema(description = "Quote Article quantity", example = "3") int quantity,
        @Schema(description = "Quote Article price", example = "49.99") Double price,
        @Schema(description = "Quote Article currency", example = "USD", allowableValues = {
                "USD", "EUR", "CAD", "GBP"}) Currency currency,
        @Schema(description = "Quote Article TAX Percent", example = "20%") BigDecimal vatPercent)
        implements
            Request<UpdateQuoteArticleCommand>{

    @Override
    public UpdateQuoteArticleCommand toCommand() {
        if (!isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters");
        if (quantity <= 0)
            throw new InvalidRequestException("Quantity must be greater than 0 and cannot exceed 100,000");
        if (!isValidMoneyPrice(price))
            throw new InvalidRequestException("Price must be greater than 0 and cannot exceed 100,000");
        if (!isValidCurrency(currency))
            throw new InvalidRequestException("Currency is not valid");
        if (!isValidBigDecimal(vatPercent))
            throw new InvalidRequestException("VAT is not valid");

        return new UpdateQuoteArticleCommand(description.trim(), quantity, price, currency, vatPercent);
    }

}
