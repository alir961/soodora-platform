package com.soodora.domain.quotearticle;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteArticleRepository {

    QuoteArticleDTO save(CreateQuoteArticleCommand command);
    QuoteArticleDTO update(UpdateQuoteArticleCommand command, String slug);
    QuoteArticleDTO findBySlug(String slug);
    Page<QuoteArticleDTO> findAll(Pageable pageable);
    List<QuoteArticleDTO> findAll();
    void deleteArticleQuoteBySlug(String slug);

}
