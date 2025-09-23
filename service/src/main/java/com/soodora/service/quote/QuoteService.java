package com.soodora.service.quote;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.quote.*;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.quotearticle.QuoteArticleRepository;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.domain.user.UserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteArticleRepository quoteArticleRepository;

    public QuoteDTO createQuote(CreateQuoteCommand command, UserDTO user) {
        if (quoteRepository.isQuoteNameUsedByUser(command.name(), user.slug())) {
            throw new QuoteUnityException("Quote name is already used");
        }
        return quoteRepository.save(command, user);
    }

    public QuoteDTO updateQuote(UpdateQuoteCommand command, String slug) {
        try {
            return quoteRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
    }

    public QuoteDTO getQuote(String slug) {
        try {
            return quoteRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
    }

    public Page<QuoteDTO> getAllQuotes(Pageable pageable) {
        return quoteRepository.findAll(pageable);
    }

    public Page<QuoteDTO> getQuotesByFilter(QuoteFilter filter, Pageable pageable) {
        boolean isFilterEmpty = filter.isEmpty();
        if (isFilterEmpty) {
            return getAllQuotes(pageable);
        } else {
            return quoteRepository.findByFilter(filter, pageable);
        }
    }

    public void deleteQuote(String slug) {
        try {
            quoteRepository.deleteQuoteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
    }

    public QuoteArticleDTO addQuoteArticle(CreateQuoteArticleCommand command) {
        return quoteArticleRepository.save(command);
    }

    public QuoteArticleDTO updateQuoteArticle(UpdateQuoteArticleCommand command, String slug) {
        try {
            return quoteArticleRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
    }

    public QuoteArticleDTO getQuoteArticle(String slug) {
        try {
            return quoteArticleRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
    }

    public Page<QuoteArticleDTO> getAllQuoteArticles(Pageable pageable) {
        return quoteArticleRepository.findAll(pageable);
    }

    public void deleteQuoteArticle(String slug) {
        try {
            quoteArticleRepository.deleteArticleQuoteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
    }

}
