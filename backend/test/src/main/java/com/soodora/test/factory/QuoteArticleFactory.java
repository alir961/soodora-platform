package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class QuoteArticleFactory {

    public static QuoteArticleDTO buildDefaultQuoteArticle() {
        return new QuoteArticleDTO(
                "quoteSlug",
                "articleSlug",
                "Sample Article Description",
                2,
                Money.of(BigDecimal.valueOf(50), Currency.EUR),
                BigDecimal.valueOf(20),
                Money.of(BigDecimal.valueOf(100), Currency.EUR));
    }
}
