package com.soodora.infrastructure.linkpage;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkPageUnityException;
import com.soodora.domain.linkpage.CreateLinkPageCommand;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.linkpage.LinkPageRepository;
import com.soodora.domain.linkpage.UpdateLinkPageCommand;
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
public class LinkPageRepositoryImpl implements LinkPageRepository {

    private final LinkPageJpaRepository repository;
    private final ArtistJpaRepository artistsRepository;

    @Override
    @Transactional
    public LinkPageDTO save(CreateLinkPageCommand command, User user) {
        ArtistEntity artistEntity = artistsRepository.findByUserSlug(user.getSlug())
                .orElseThrow(
                        () -> new UnknownSlugException("Unable to find artist with user slug: #" + user.getSlug()));
        Optional<LinkPageEntity> existingLinkPage = repository.findByArtistUserSlug(artistEntity.getUser().getSlug());

        if (existingLinkPage.isEmpty()) {
            LinkPageEntity entity = new LinkPageEntity();

            entity.setTitle(command.title());
            entity.setDescription(command.description());
            entity.setSlug(generateUniqueSlug());
            entity.setArtist(artistEntity);

            return repository.save(entity).aggregate();
        }
        throw new LinkPageUnityException("Artist with user slug #" + user.getSlug() + " already has a link page.");
    }

    @Override
    @Transactional
    public LinkPageDTO update(UpdateLinkPageCommand command, String slug) {
        LinkPageEntity existingLinkPage = findLinkPageBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            existingLinkPage.setTitle(command.title());
        if (isNotNullOrBlank(command.description()))
            existingLinkPage.setDescription(command.description());
        return repository.save(existingLinkPage).aggregate();
    }

    @Override
    @Transactional
    public LinkPageDTO findBySlug(String slug) {
        return findLinkPageBySlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public List<LinkPageDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        LinkPageEntity linkPage = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link page with slug: #" + slug));

        linkPage.getArtist().setLinkPage(null);
        repository.delete(linkPage);
    }

    protected LinkPageEntity findLinkPageBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link page with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
