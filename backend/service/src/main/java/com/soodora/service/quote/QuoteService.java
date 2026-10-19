package com.soodora.service.quote;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.quote.*;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.quotearticle.QuoteArticleRepository;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteArticleRepository quoteArticleRepository;
    private final PdfExporter pdfExporter;

    public QuoteDTO createQuote(CreateQuoteCommand command, User user) {
        if (quoteRepository.isQuoteNameUsedByUser(command.name(), user.getSlug())) {
            throw new QuoteUnityException("Quote name is already used");
        }
        return quoteRepository.save(command, user);
    }

    public QuoteDTO updateQuote(UpdateQuoteCommand command, String slug, User user) {
        getQuote(slug, user);
        try {
            return quoteRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
    }

    /**
     * Quotes hold the artist's client data: only their owner (or an admin) can
     * access them.
     */
    public QuoteDTO getQuote(String slug, User user) {
        QuoteDTO quote;

        try {
            quote = quoteRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
        if (!user.isAdmin() && !quote.artist().slug().equals(user.getSlug()))
            throw new InvalidPermissionException("Only the owner can access this quote");
        return quote;
    }

    public Page<QuoteDTO> getQuotesByFilter(QuoteFilter filter, User user, Pageable pageable) {
        if (!user.isAdmin()) {
            // Artists only ever search their own quotes, whatever userSlug they send
            filter = new QuoteFilter(Optional.of(user.getSlug()), filter.name(), filter.description(),
                    filter.clientName());
        }
        if (filter.isEmpty())
            return quoteRepository.findAll(pageable);
        return quoteRepository.findByFilter(filter, pageable);
    }

    public void deleteQuote(String slug, User user) {
        getQuote(slug, user);
        try {
            quoteRepository.deleteQuoteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteException(exception.getMessage());
        }
    }

    public QuoteArticleDTO addQuoteArticle(CreateQuoteArticleCommand command, User user) {
        getQuote(command.quoteSlug(), user);
        return quoteArticleRepository.save(command);
    }

    public QuoteArticleDTO updateQuoteArticle(UpdateQuoteArticleCommand command, String slug, User user) {
        getQuoteArticle(slug, user);
        try {
            return quoteArticleRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
    }

    public QuoteArticleDTO getQuoteArticle(String slug, User user) {
        QuoteArticleDTO article;

        try {
            article = quoteArticleRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
        getQuote(article.quoteSlug(), user);
        return article;
    }

    public Page<QuoteArticleDTO> getAllQuoteArticles(User user, Pageable pageable) {
        if (user.isAdmin())
            return quoteArticleRepository.findAll(pageable);
        Set<String> ownQuotes = quoteRepository
                .findByFilter(new QuoteFilter(Optional.of(user.getSlug()), Optional.empty(), Optional.empty(),
                        Optional.empty()), Pageable.unpaged())
                .stream()
                .map(QuoteDTO::slug)
                .collect(Collectors.toSet());
        List<QuoteArticleDTO> articles = quoteArticleRepository.findAll()
                .stream()
                .filter(article -> ownQuotes.contains(article.quoteSlug()))
                .toList();

        if (pageable.isUnpaged())
            return new PageImpl<>(articles);
        int from = (int) Math.min(pageable.getOffset(), articles.size());
        int to = Math.min(from + pageable.getPageSize(), articles.size());
        return new PageImpl<>(articles.subList(from, to), pageable, articles.size());
    }

    public void deleteQuoteArticle(String slug, User user) {
        getQuoteArticle(slug, user);
        try {
            quoteArticleRepository.deleteArticleQuoteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownQuoteArticleException(exception.getMessage());
        }
    }

    public byte[] exportQuoteToPdf(String slug, User user) {
        QuoteDTO quote = getQuote(slug, user);

        try {
            List<QuoteArticleDTO> quoteItems = quoteArticleRepository.findAll()
                    .stream()
                    .filter(i -> i.quoteSlug().equals(slug))
                    .toList();

            return pdfExporter.exportQuote(quote, quoteItems)
                    .toByteArray();
        } catch (QuoteGenerationException e) {
            throw new InvalidRequestException("Failed to export Quote to PDF: " + e.getMessage());
        }
    }

}
