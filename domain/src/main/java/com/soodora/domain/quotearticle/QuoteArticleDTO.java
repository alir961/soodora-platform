package com.soodora.domain.quotearticle;

import com.soodora.domain.common.Money;

import java.math.BigDecimal;

public record QuoteArticleDTO(String quoteSlug, String slug, String description, int quantity, Money unitPrice,
        BigDecimal vatPercent, Money totalPrice) {
}
