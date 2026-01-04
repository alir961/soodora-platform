package com.soodora.infrastructure.linkhub;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkHubUnityException;
import com.soodora.domain.linkhub.*;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class LinkHubRepositoryImpl implements LinkHubRepository {

    private final LinkHubJpaRepository linkHubRepository;
    private final ArtistJpaRepository artistRepository;

    @Override
    @Transactional
    public LinkHub save(User user, CreateLinkHubCommand command) {
        ArtistEntity artistEntity = artistRepository.findByUserSlug(user.getSlug())
                .orElseThrow(
                        () -> new UnknownSlugException("Unable to find artist with user slug: #" + user.getSlug()));
        Optional<LinkHubEntity> existingLinkHub = linkHubRepository
                .findByArtistUserSlug(artistEntity.getUser().getSlug());

        if (existingLinkHub.isEmpty()) {
            LinkHubEntity entity = new LinkHubEntity();

            entity.setTitle(command.title());
            entity.setDescription(command.description());
            entity.setSlug(generateUniqueSlug());
            entity.setArtist(artistEntity);

            return linkHubRepository.save(entity).aggregate();
        }
        throw new LinkHubUnityException("Artist with user slug #" + user.getSlug() + " already has a link hub.");
    }

    @Override
    @Transactional
    public LinkHub findBySlug(String linkHubSlug) {
        return findLinkHubBySlugOrThrow(linkHubSlug)
                .aggregate();
    }

    @Override
    @Transactional
    public List<LinkHub> findAll() {
        return linkHubRepository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public LinkHub update(String linkHubSlug, UpdateLinkHubCommand command) {
        LinkHubEntity existingLinkHub = findLinkHubBySlugOrThrow(linkHubSlug);

        if (isNotNullOrBlank(command.title()))
            existingLinkHub.setTitle(command.title());
        if (isNotNullOrBlank(command.description()))
            existingLinkHub.setDescription(command.description());
        return linkHubRepository.save(existingLinkHub)
                .aggregate();
    }

    @Override
    @Transactional
    public void deleteBySlug(String linkHubSlug) {
        LinkHubEntity linkHub = findLinkHubBySlugOrThrow(linkHubSlug);

        linkHub.getArtist().setLinkHub(null);
        linkHubRepository.delete(linkHub);
    }

    @Override
    @Transactional
    public void persistView(String linkHubSlug) {
        LinkHubEntity linkHub = findLinkHubBySlugOrThrow(linkHubSlug);

        linkHub.setViewCount(linkHub.getViewCount() + 1);
        linkHubRepository.save(linkHub);
    }

    protected LinkHubEntity findLinkHubBySlugOrThrow(String slug) {
        return linkHubRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link hub with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (linkHubRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
