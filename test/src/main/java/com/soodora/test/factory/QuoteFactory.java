package com.soodora.test.factory;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class QuoteFactory {

    public static QuoteDTO buildDefaultQuote(String id) {
        ArtistDTO artist = ArtistFactory.buildArtistDTO("123");

        List<QuoteArticleDTO> articles = Collections.singletonList(
                QuoteArticleFactory.buildDefaultQuoteArticle());

        Date now = new Date();

        return new QuoteDTO(
                "quoteSlug" + id,
                artist,
                "Sample Quote",
                "Client Name",
                "Client Address",
                "client@email.com",
                "1234567890",
                "Quote Description",
                articles,
                Money.of(BigDecimal.valueOf(200), Currency.EUR),
                now,
                now);
    }

}
