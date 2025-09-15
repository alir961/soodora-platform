package com.soodora.infrastructure.mediakititem;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
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

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class MediaKitItemRepositoryImpl implements MediaKitItemRepository {

    private final MediaKitItemJpaRepository repository;
    private final MediaKitJpaRepository mediaKitRepository;
    private final ImageStorageUploader imageStorageUploader;

    @Override
    @Transactional
    public MediaKitItemDTO save(CreateMediaKitItemCommand command, String artistSlug, FileData fileData) {
        MediaKitEntity mediaKit = mediaKitRepository.findByArtistUserSlug(artistSlug)
                .orElseThrow(() -> new UnknownSlugException("Media Kit not found for artist with slug #" + artistSlug));
        MediaKitItemEntity mediaKitItem = new MediaKitItemEntity();

        mediaKitItem.setTitle(command.title());
        mediaKitItem.setType(command.type());

        if ("IMAGE".equalsIgnoreCase(command.type()) && fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            mediaKitItem.setContent(imageUrl);
        } else {
            mediaKitItem.setContent(command.content());
        }

        mediaKitItem.setSlug(generateUniqueSlug());
        mediaKitItem.setMediaKit(mediaKit);

        mediaKit.getItems().add(mediaKitItem);

        return repository.save(mediaKitItem).toDTO();
    }

    @Override
    @Transactional
    public MediaKitItemDTO update(UpdateMediaKitItemCommand command, String slug, FileData fileData) {
        MediaKitItemEntity existingMediaKitItem = findMediaKitItemBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            existingMediaKitItem.setTitle(command.title());
        if (isNotNullOrBlank(command.type()))
            existingMediaKitItem.setType(command.type());
        if (isNotNullOrBlank(command.content()))
            existingMediaKitItem.setContent(command.content());
        if ("IMAGE".equalsIgnoreCase(command.type()) && fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            existingMediaKitItem.setContent(imageUrl);
        }
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
