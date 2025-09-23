package com.soodora.domain.quote;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.MoneyDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.util.Date;
import java.util.List;

public record QuoteDTO(
        String slug,
        ArtistDTO artist,
        String name,
        String clientName,
        String clientAddress,
        String clientEmail,
        String clientPhone,
        String description,
        List<QuoteArticleDTO> quoteArticles,
        MoneyDTO Total,
        Date createdAtDate,
        Date updatedAt) {
}
