package com.soodora.infrastructure.quotearticle;

import com.soodora.domain.common.SlugGenerator;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.quotearticle.QuoteArticleRepository;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.infrastructure.quote.QuoteEntity;
import com.soodora.infrastructure.quote.QuoteJpaRepository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class QuoteArticleRepositoryImpl implements QuoteArticleRepository {

    private final QuoteArticleJpaRepository quoteArticleRepository;
    private final QuoteJpaRepository quoteRepository;

    @Override
    public QuoteArticleDTO save(CreateQuoteArticleCommand command) {
        QuoteEntity quoteEntity = quoteRepository.findBySlug(command.quoteSlug())
                .orElseThrow(
                        () -> new UnknownSlugException("Quote not found for artist with slug #" + command.quoteSlug()));

        QuoteArticleEntity entity = new QuoteArticleEntity();
        entity.setQuote(quoteEntity);
        entity.setDescription(command.description());
        entity.setQuantity(command.quantity());
        entity.setUnitPrice(new MoneyEmbeddable(command.price(), command.currency()));
        entity.setVatPercent(command.vatPercent());

        entity.setSlug(generateUniqueSlug());
        return quoteArticleRepository.save(entity).toDTO();
    }

    public QuoteArticleDTO update(UpdateQuoteArticleCommand command, String slug) {
        QuoteArticleEntity existingEntity = quoteArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("QuoteArticle not found with slug: " + slug));

        if (isNotNullOrBlank(command.description()))
            existingEntity.setDescription(command.description());
        if (command.quantity() > 0)
            existingEntity.setQuantity(command.quantity());
        if (command.price().compareTo(BigDecimal.ZERO) > 0)
            existingEntity.setUnitPrice(new MoneyEmbeddable(command.price(), command.currency()));
        if (command.vatPercent() != null)
            existingEntity.setVatPercent(command.vatPercent());

        return quoteArticleRepository.save(existingEntity).toDTO();
    }

    @Override
    public QuoteArticleDTO findBySlug(String slug) {
        return quoteArticleRepository.findBySlug(slug)
                .map(QuoteArticleEntity::toDTO)
                .orElseThrow(() -> new UnknownSlugException("QuoteArticle not found with slug: " + slug));
    }

    @Override
    public Page<QuoteArticleDTO> findAll(Pageable pageable) {
        return quoteArticleRepository.findAll(pageable).map(QuoteArticleEntity::toDTO);
    }

    @Override
    public void deleteArticleQuoteBySlug(String slug) {
        QuoteArticleEntity entity = quoteArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("QuoteArticle not found with slug: " + slug));
        quoteArticleRepository.delete(entity);
    }

    private String generateUniqueSlug() {
        String slug = SlugGenerator.generateSlug();
        while (quoteArticleRepository.findBySlug(slug).isPresent()) {
            slug = SlugGenerator.generateSlug();
        }
        return slug;
    }

}
