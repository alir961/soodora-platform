package com.soodora.infrastructure.mediakititem;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.domain.mediakititem.MediaKitItemDTO;
import com.soodora.domain.mediakititem.MediaKitItemRepository;
import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.mediakit.MediaKitEntity;
import com.soodora.infrastructure.mediakit.MediaKitJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;
import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class MediaKitItemRepositoryImpl implements MediaKitItemRepository {

    private final MediaKitItemJpaRepository repository;
    private final MediaKitJpaRepository mediaKitRepository;

    @Override
    @Transactional
    public MediaKitItemDTO save(CreateMediaKitItemCommand command, String artistSlug) {
        MediaKitEntity existingMediaKit = mediaKitRepository.findByArtistUserSlug(artistSlug)
                .orElseThrow(() -> new UnknownSlugException("Media Kit not found for artist with slug #" + artistSlug));

        MediaKitItemEntity entity = new MediaKitItemEntity();
        entity.setTitle(command.title());
        entity.setType(command.type());
        entity.setContent(command.content());
        entity.setSlug(generateUniqueSlug());
        entity.setMediaKit(existingMediaKit);

        existingMediaKit.getItems().add(entity);

        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public MediaKitItemDTO update(UpdateMediaKitItemCommand command, String slug) {
        MediaKitItemEntity existingMediaKitItem = findMediaKitItemBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()) && !command.title().equals(existingMediaKitItem.getTitle()))
            existingMediaKitItem.setTitle(command.title());
        if (isNotNullOrBlank(command.type()) && !command.type().equals(existingMediaKitItem.getType()))
            existingMediaKitItem.setType(command.type());
        if (isNotNullOrBlank(command.content()) && !command.content().equals(existingMediaKitItem.getContent()))
            existingMediaKitItem.setContent(command.content());

        return repository.save(existingMediaKitItem).toDTO();
    }

    @Override
    @Transactional
    public MediaKitItemDTO findBySlug(String slug) {
        return findMediaKitItemBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<MediaKitItemDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        MediaKitItemEntity mediaKitItem = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit item with slug: #" + slug));

        repository.delete(mediaKitItem);
    }

    protected MediaKitItemEntity findMediaKitItemBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit item with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
