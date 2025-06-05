package com.grooveconductor.infrastructure.link;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.link.CreateLinkCommand;
import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.link.LinkRepository;
import com.grooveconductor.domain.link.UpdateLinkCommand;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.linkpage.LinkPageEntity;
import com.grooveconductor.infrastructure.linkpage.LinkPageJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class LinkRepositoryImpl implements LinkRepository {

    private final LinkJpaRepository repository;
    private final LinkPageJpaRepository linkPageRepository;

    protected LinkEntity findLinkBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link with slug: #" + slug));
    }

    @Override
    public LinkDTO save(CreateLinkCommand command, String userSlug) {
        LinkPageEntity linkPage = linkPageRepository.findByUserSlug(userSlug)
                .orElseThrow(() -> new UnknownSlugException("Link page not found for user slug: " + userSlug));

        LinkEntity entity = new LinkEntity();
        entity.setTitle(command.title());
        entity.setDescription(command.description());
        entity.setUrl(command.url());
        entity.setIconUrl(command.iconUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setLinkPage(linkPage);

        linkPage.getLinks().add(entity);

        return repository.save(entity).toDTO();
    }

    @Override
    public LinkDTO update(UpdateLinkCommand command, String slug) {
        LinkEntity existingLink = findLinkBySlugOrThrow(slug);

        if (command.getTitle() != null && !command.getTitle().isBlank()
                && !command.getTitle().equals(existingLink.getTitle()))
            existingLink.setTitle(command.getTitle());
        if (command.getDescription() != null && !command.getDescription().isBlank()
                && !command.getDescription().equals(existingLink.getDescription()))
            existingLink.setDescription(command.getDescription());
        if (command.getUrl() != null && !command.getUrl().isBlank()
                && !command.getUrl().equals(existingLink.getUrl()))
            existingLink.setUrl(command.getUrl());
        if (command.getIconUrl() != null && !command.getIconUrl().isBlank()
                && !command.getIconUrl().equals(existingLink.getIconUrl()))
            existingLink.setIconUrl(command.getIconUrl());
        return repository.save(existingLink).toDTO();
    }

    @Override
    public LinkDTO findBySlug(String slug) {
        return findLinkBySlugOrThrow(slug).toDTO();
    }

    @Override
    public List<LinkDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public void deleteBySlug(String slug) {
        LinkEntity link = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link with slug: #" + slug));

        repository.deleteById(link.getId());
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
