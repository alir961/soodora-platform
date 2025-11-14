package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkhub.LinkHubDTO;
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
    public List<EventDTO> retrieveEvents(String userSlug) {
        ArtistEntity entity = findArtistByUserSlug(userSlug);

        return entity.getEvents().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public LinkHubDTO retrieveLinkHub(String userSlug) throws LinkHubNotFoundException {
        ArtistEntity artist = findArtistByUserSlug(userSlug);

        if (artist.getLinkHub() == null)
            throw new LinkHubNotFoundException("Link Hub not found for artist " + userSlug);
        return artist.getLinkHub().aggregate();
    }

    private ArtistEntity findArtistByUserSlug(String slug) throws UnknownIdentifierException {
        return repository.findByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist '" + slug + "'"));
    }

}
