package com.soodora.infrastructure.link;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.linkpage.LinkPageEntity;
import com.soodora.infrastructure.linkpage.LinkPageJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;
import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

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
    @Transactional
    public LinkDTO save(CreateLinkCommand command, String userSlug) {
        LinkPageEntity linkPage = linkPageRepository.findByArtistUserSlug(userSlug)
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
    @Transactional
    public LinkDTO update(UpdateLinkCommand command, String slug) {
        LinkEntity existingLink = findLinkBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            existingLink.setTitle(command.title());
        if (isNotNullOrBlank(command.description()))
            existingLink.setDescription(command.description());
        if (isNotNullOrBlank(command.url()))
            existingLink.setUrl(command.url());
        if (isNotNullOrBlank(command.iconUrl()))
            existingLink.setIconUrl(command.iconUrl());
        return repository.save(existingLink).toDTO();
    }

    @Override
    @Transactional
    public LinkDTO findBySlug(String slug) {
        return findLinkBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<LinkDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
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
