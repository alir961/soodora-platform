package com.soodora.domain.quote;

import com.soodora.domain.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteRepository {

    QuoteDTO save(CreateQuoteCommand command, User user);
    QuoteDTO update(UpdateQuoteCommand command, String slug);
    QuoteDTO findBySlug(String slug);
    Page<QuoteDTO> findAll(Pageable pageable);
    Page<QuoteDTO> findByFilter(QuoteFilter filter, Pageable pageable);
    void deleteQuoteBySlug(String slug);
    boolean isQuoteNameUsedByUser(String name, String userSlug);

}
