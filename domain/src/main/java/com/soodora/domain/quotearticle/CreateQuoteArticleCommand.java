package com.soodora.domain.quotearticle;

import com.soodora.domain.common.Currency;

import java.math.BigDecimal;

public record CreateQuoteArticleCommand(
        String quoteSlug,
        String description,
        int quantity,
        BigDecimal price,
        Currency currency,
        BigDecimal vatPercent) {
}
