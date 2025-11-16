package com.soodora.infrastructure.mediakititem;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.mediakititem.MediaKitItemRepository;
import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.infrastructure.mediakit.MediaKitEntity;
import com.soodora.infrastructure.mediakit.MediaKitJpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class MediaKitItemRepositoryImpl implements MediaKitItemRepository {

    private final MediaKitJpaRepository mediaKitRepository;
    private final MediaKitItemJpaRepository mediaKitItemRepository;
    private final ImageStorageUploader imageStorageUploader;

    @Override
    @Transactional
    public MediaKitItem save(CreateMediaKitItemCommand command, String artistSlug, FileData fileData) {
        MediaKitEntity mediaKit = mediaKitRepository.findByArtistUserSlug(artistSlug)
                .orElseThrow(() -> new UnknownSlugException("Media Kit not found for artist with slug #" + artistSlug));
        MediaKitItemEntity mediaKitItem = new MediaKitItemEntity();

        mediaKitItem.setTitle(command.title());
        mediaKitItem.setType(command.type());

        if (command.type().equalsIgnoreCase("IMAGE") && fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);

            mediaKitItem.setContent(imageUrl);
        } else {
            mediaKitItem.setContent(command.content());
        }

        mediaKitItem.setSlug(generateUniqueSlug());
        mediaKitItem.setMediaKit(mediaKit);

        mediaKit.getItems().add(mediaKitItem);

        return mediaKitItemRepository.save(mediaKitItem).aggregate();
    }

    @Override
    @Transactional
    public MediaKitItem update(UpdateMediaKitItemCommand command, String slug, FileData fileData) {
        MediaKitItemEntity existingMediaKitItem = findMediaKitItemBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            existingMediaKitItem.setTitle(command.title());
        if (isNotNullOrBlank(command.type()))
            existingMediaKitItem.setType(command.type());
        if (isNotNullOrBlank(command.content()))
            existingMediaKitItem.setContent(command.content());
        if (command.type().equalsIgnoreCase("IMAGE") && fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);

            existingMediaKitItem.setContent(imageUrl);
        }
        return mediaKitItemRepository.save(existingMediaKitItem).aggregate();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        MediaKitItemEntity mediaKitItem = mediaKitItemRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit item with slug: #" + slug));

        mediaKitItemRepository.delete(mediaKitItem);
    }

    private MediaKitItemEntity findMediaKitItemBySlugOrThrow(String slug) {
        return mediaKitItemRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit item with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (mediaKitItemRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
