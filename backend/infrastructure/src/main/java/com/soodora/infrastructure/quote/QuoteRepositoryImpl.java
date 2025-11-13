package com.soodora.infrastructure.quote;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.quote.*;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static java.util.Collections.emptyList;

import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class QuoteRepositoryImpl implements QuoteRepository {

    private final QuoteJpaRepository quoteJpaRepository;
    private final ArtistJpaRepository artistRepository;

    @Override
    @Transactional
    public QuoteDTO save(CreateQuoteCommand command, User user) {
        ArtistEntity artistEntity = artistRepository.findByUserSlug(user.getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist with user slug #" + user.getSlug()));

        QuoteEntity entity = new QuoteEntity();
        entity.setSlug(generateUniqueSlug());
        entity.setArtist(artistEntity);
        entity.setName(command.name());
        entity.setClientName(command.clientName());
        entity.setClientEmail(command.clientEmail());
        entity.setClientAddress(command.clientAddress());
        entity.setClientPhone(command.clientPhone());
        entity.setDescription(command.description());
        entity.setArticles(emptyList());

        return quoteJpaRepository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public QuoteDTO update(UpdateQuoteCommand command, String slug) {
        QuoteEntity entity = quoteJpaRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Quote not found: " + slug));

        if (isNotNullOrBlank(command.name()))
            entity.setName(command.name());
        if (isNotNullOrBlank(command.clientName()))
            entity.setClientName(command.clientName());
        if (isNotNullOrBlank(command.clientEmail()))
            entity.setClientEmail(command.clientEmail());
        if (isNotNullOrBlank(command.clientAddress()))
            entity.setClientAddress(command.clientAddress());
        if (isNotNullOrBlank(command.clientPhone()))
            entity.setClientPhone(command.clientPhone());
        if (isNotNullOrBlank(command.description()))
            entity.setDescription(command.description());

        return quoteJpaRepository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public QuoteDTO findBySlug(String slug) {
        return quoteJpaRepository.findBySlug(slug)
                .map(DomainEntityAdapter::aggregate)
                .orElseThrow(() -> new UnknownSlugException("Quote not found: " + slug));
    }

    @Override
    @Transactional
    public Page<QuoteDTO> findAll(Pageable pageable) {
        return quoteJpaRepository.findAll(pageable).map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<QuoteDTO> findByFilter(QuoteFilter filter, Pageable pageable) {
        var spec = QuoteSpecification.build(filter);
        return quoteJpaRepository.findAll(spec, pageable).map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public void deleteQuoteBySlug(String slug) {
        QuoteEntity entity = quoteJpaRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Quote not found: " + slug));
        quoteJpaRepository.delete(entity);
    }

    @Override
    public boolean isQuoteNameUsedByUser(String name, String userSlug) {
        List<QuoteEntity> userQuotes = findQuoteByUserSlugOrThrow(userSlug);

        return userQuotes.stream()
                .anyMatch(quotes -> quotes.getName().equalsIgnoreCase(name));
    }

    protected List<QuoteEntity> findQuoteByUserSlugOrThrow(String slug) {
        return quoteJpaRepository.findQuotesByArtistUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find quote with user slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = com.soodora.domain.common.SlugGenerator.generateSlug();
        while (quoteJpaRepository.findBySlug(slug).isPresent())
            slug = com.soodora.domain.common.SlugGenerator.generateSlug();
        return slug;
    }

}
