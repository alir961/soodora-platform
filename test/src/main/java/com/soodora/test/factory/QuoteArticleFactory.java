package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.MoneyDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class QuoteArticleFactory {

    public static QuoteArticleDTO buildDefaultQuoteArticle() {
        MoneyDTO unitPrice = new MoneyDTO(BigDecimal.valueOf(50.0), Currency.EUR, Currency.EUR.getSymbol());
        MoneyDTO totalPrice = new MoneyDTO(BigDecimal.valueOf(100.0), Currency.EUR, Currency.EUR.getSymbol());

        return new QuoteArticleDTO(
                "quoteSlug",
                "articleSlug",
                "Sample Article Description",
                2,
                unitPrice,
                BigDecimal.valueOf(20),
                totalPrice);
    }
}
