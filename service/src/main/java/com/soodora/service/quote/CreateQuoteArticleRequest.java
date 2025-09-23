package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.math.BigDecimal;

public record CreateQuoteArticleRequest(
        String quoteSlug,
        String description,
        int quantity,
        Double price,
        Currency currency,
        BigDecimal vatPercent)
        implements
            Request<CreateQuoteArticleCommand> {

    @Override
    public CreateQuoteArticleCommand toCommand() {
        if (quoteSlug == null || quoteSlug.isEmpty())
            throw new InvalidRequestException("Quote slug is not valid");
        if (description == null || description.isEmpty())
            throw new InvalidRequestException("Description is not valid");
        if (quantity == 0)
            throw new InvalidRequestException("Quantity is not valid");
        if (price == null || price <= 0)
            throw new InvalidRequestException("Price is not valid");
        if (currency == null)
            throw new InvalidRequestException("Currency is not valid");
        if (vatPercent == null || vatPercent.equals(BigDecimal.ZERO))
            throw new InvalidRequestException("VAT is not valid");

        return new CreateQuoteArticleCommand(quoteSlug, description.trim(), quantity, price, currency, vatPercent);
    }

}
