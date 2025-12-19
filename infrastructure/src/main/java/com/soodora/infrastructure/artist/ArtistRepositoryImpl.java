package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.Event;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.user.UserEntity;

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
    public User find(String slug) {
        return findArtistBySlugOrThrows(slug)
                .getUser()
                .aggregate();
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(ArtistEntity::getUser)
                .map(UserEntity::aggregateFollowers)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String slug) {
        ArtistEntity entity = findArtistBySlugOrThrows(slug);

        repository.delete(entity);
    }

    @Override
    @Transactional
    public List<Event> retrieveEvents(String userSlug) {
        ArtistEntity entity = findArtistBySlugOrThrows(userSlug);

        return entity.getEvents().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public LinkHub retrieveLinkHub(String artistSlug) throws ArtistWithoutLinkHubException {
        ArtistEntity artist = findArtistBySlugOrThrows(artistSlug);

        if (artist.getLinkHub() == null)
            throw new ArtistWithoutLinkHubException("Link Hub not found for artist " + artistSlug);
        return artist.getLinkHub().aggregate();
    }

    private ArtistEntity findArtistBySlugOrThrows(String slug) throws UnknownIdentifierException {
        return repository.findByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist '" + slug + "'"));
    }

}
