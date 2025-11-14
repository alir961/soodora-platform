package com.soodora.infrastructure.linkhub;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkHubUnityException;
import com.soodora.domain.linkhub.CreateLinkHubCommand;
import com.soodora.domain.linkhub.LinkHubDTO;
import com.soodora.domain.linkhub.LinkHubRepository;
import com.soodora.domain.linkhub.UpdateLinkHubCommand;
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

    private final LinkHubJpaRepository repository;
    private final ArtistJpaRepository artistsRepository;

    @Override
    @Transactional
    public LinkHubDTO save(CreateLinkHubCommand command, User user) {
        ArtistEntity artistEntity = artistsRepository.findByUserSlug(user.getSlug())
                .orElseThrow(
                        () -> new UnknownSlugException("Unable to find artist with user slug: #" + user.getSlug()));
        Optional<LinkHubEntity> existingLinkHub = repository.findByArtistUserSlug(artistEntity.getUser().getSlug());

        if (existingLinkHub.isEmpty()) {
            LinkHubEntity entity = new LinkHubEntity();

            entity.setTitle(command.title());
            entity.setDescription(command.description());
            entity.setSlug(generateUniqueSlug());
            entity.setArtist(artistEntity);

            return repository.save(entity).aggregate();
        }
        throw new LinkHubUnityException("Artist with user slug #" + user.getSlug() + " already has a link hub.");
    }

    @Override
    @Transactional
    public LinkHubDTO update(UpdateLinkHubCommand command, String slug) {
        LinkHubEntity existingLinkHub = findLinkHubBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            existingLinkHub.setTitle(command.title());
        if (isNotNullOrBlank(command.description()))
            existingLinkHub.setDescription(command.description());
        return repository.save(existingLinkHub).aggregate();
    }

    @Override
    @Transactional
    public LinkHubDTO findBySlug(String slug) {
        return findLinkHubBySlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public List<LinkHubDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        LinkHubEntity linkHub = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link hub with slug: #" + slug));

        linkHub.getArtist().setLinkHub(null);
        repository.delete(linkHub);
    }

    protected LinkHubEntity findLinkHubBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link hub with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
