package com.soodora.domain.generator;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakit.MediaKitGenerationException;
import com.soodora.domain.mediakititem.MediaKitItemDTO;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quote.QuoteGenerationException;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface PdfExporter {

    ByteArrayOutputStream exportMediaKit(MediaKitDTO mediaKit, List<MediaKitItemDTO> items)
            throws MediaKitGenerationException;

    ByteArrayOutputStream exportQuote(QuoteDTO quote, List<QuoteArticleDTO> quoteArticles)
            throws QuoteGenerationException;

}
