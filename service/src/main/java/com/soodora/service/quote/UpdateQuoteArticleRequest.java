package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.math.BigDecimal;

public record UpdateQuoteArticleRequest(
        String description,
        int quantity,
        Double price,
        Currency currency,
        BigDecimal vatPercent)
        implements
            Request<UpdateQuoteArticleCommand> {

    @Override
    public UpdateQuoteArticleCommand toCommand() {
        if (price == null || price <= 0)
            throw new InvalidRequestException("Price is not valid");
        if (currency == null)
            throw new InvalidRequestException("Currency is not valid");
        if (vatPercent == null || vatPercent.equals(BigDecimal.ZERO))
            throw new InvalidRequestException("VAT is not valid");

        return new UpdateQuoteArticleCommand(description.trim(), quantity, price, currency, vatPercent);
    }

}
