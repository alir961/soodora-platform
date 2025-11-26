package com.soodora.infrastructure.link;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.Link;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.linkhub.LinkHubEntity;
import com.soodora.infrastructure.linkhub.LinkHubJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class LinkRepositoryImpl implements LinkRepository {

    private final LinkJpaRepository repository;
    private final LinkHubJpaRepository linkHubRepository;

    protected LinkEntity findLinkBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link with slug: #" + slug));
    }

    @Override
    @Transactional
    public Link save(String artistSlug, CreateLinkCommand command) {
        LinkHubEntity linkHub = linkHubRepository.findByArtistUserSlug(artistSlug)
                .orElseThrow(
                        () -> new ArtistWithoutLinkHubException("Link hub not found for user slug: " + artistSlug));
        LinkEntity entity = new LinkEntity();

        entity.setTitle(command.title());
        entity.setDescription(command.description());
        entity.setUrl(command.url());
        entity.setIconUrl(command.iconUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setLinkHub(linkHub);

        linkHub.getLinks().add(entity);

        return repository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public Link update(String linkSlug, UpdateLinkCommand command) {
        LinkEntity existingLink = findLinkBySlugOrThrow(linkSlug);

        if (isNotNullOrBlank(command.title()))
            existingLink.setTitle(command.title());
        if (isNotNullOrBlank(command.description()))
            existingLink.setDescription(command.description());
        if (isNotNullOrBlank(command.url()))
            existingLink.setUrl(command.url());
        if (isNotNullOrBlank(command.iconUrl()))
            existingLink.setIconUrl(command.iconUrl());
        return repository.save(existingLink).aggregate();
    }

    @Override
    @Transactional
    public Link findBySlug(String linkSlug) {
        return findLinkBySlugOrThrow(linkSlug).aggregate();
    }

    @Override
    @Transactional
    public List<Link> findAll() {
        return repository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String linkSlug) {
        LinkEntity link = findLinkBySlugOrThrow(linkSlug);

        repository.deleteById(link.getId());
    }

    @Override
    @Transactional
    public LinkHub findLinkHubByLinkSlug(String linkSlug) {
        LinkEntity link = findLinkBySlugOrThrow(linkSlug);

        return link.getLinkHub().aggregate();
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
