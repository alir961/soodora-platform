package com.grooveconductor.infrastructure.linkpage;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.linkpage.CreateLinkPageCommand;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.linkpage.LinkPageRepository;
import com.grooveconductor.domain.linkpage.UpdateLinkPageCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class LinkPageRepositoryImpl implements LinkPageRepository {

    private final LinkPageJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public LinkPageDTO save(CreateLinkPageCommand command, UserDTO user) {
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user with slug: #" + user.slug()));

        Optional<LinkPageEntity> existingLinkPage = repository.findByUserSlug(userEntity.getSlug());
        if (existingLinkPage.isPresent())
            throw new IllegalStateException("User with slug #" + user.slug() + " already has a LinkPage.");

        LinkPageEntity entity = new LinkPageEntity();
        entity.setTitle(command.title());
        entity.setDescription(command.description());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);

        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public LinkPageDTO update(UpdateLinkPageCommand command, String slug) {
        LinkPageEntity existingLinkPage = findLinkPageBySlugOrThrow(slug);

        if (command.getTitle() != null && !command.getTitle().isBlank()
                && !command.getTitle().equals(existingLinkPage.getTitle()))
            existingLinkPage.setTitle(command.getTitle());
        if (command.getDescription() != null && !command.getDescription().isBlank()
                && !command.getDescription().equals(existingLinkPage.getDescription()))
            existingLinkPage.setDescription(command.getDescription());

        return repository.save(existingLinkPage).toDTO();
    }

    @Override
    @Transactional
    public LinkPageDTO findBySlug(String slug) {
        return findLinkPageBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<LinkPageDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        LinkPageEntity linkPage = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find link page with slug: #" + slug));

        linkPage.getUser().setLinkPage(null);
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
