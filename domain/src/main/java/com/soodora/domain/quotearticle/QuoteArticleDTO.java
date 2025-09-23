package com.soodora.domain.quotearticle;

import com.soodora.domain.common.MoneyDTO;

import java.math.BigDecimal;

public record QuoteArticleDTO(String quoteSlug, String slug, String description, int quantity, MoneyDTO unitPrice,
        BigDecimal vatPercent, MoneyDTO totalPrice) {
}
