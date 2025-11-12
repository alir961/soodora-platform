package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.user.*;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository repository;

    @Override
    @Transactional
    public ArtistDTO find(String slug) {
        return findArtistByUserSlug(slug)
                .aggregate();
    }

    @Override
    @Transactional
    public List<ArtistDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String slug) {
        ArtistEntity entity = findArtistByUserSlug(slug);

        repository.delete(entity);
    }

    @Override
    @Transactional
    public List<EventDTO> retrieveEvents(String slug) {
        ArtistEntity entity = findArtistByUserSlug(slug);

        return entity.getEvents().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public LinkPageDTO retrieveLinkPage(String slug) throws LinkPageNotFoundException {
        ArtistEntity artist = findArtistByUserSlug(slug);

        if (artist.getLinkPage() == null)
            throw new LinkPageNotFoundException("Link Page not found for artist " + slug);
        return artist.getLinkPage().aggregate();
    }

    private ArtistEntity findArtistByUserSlug(String slug) throws UnknownIdentifierException {
        return repository.findByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist '" + slug + "'"));
    }

}
