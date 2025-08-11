package com.soodora.infrastructure.mediakit;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakit.MediaKitRepository;
import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class MediaKitRepositoryImpl implements MediaKitRepository {

    private final MediaKitJpaRepository repository;
    private final ArtistJpaRepository artistsRepository;

    @Override
    @Transactional
    public MediaKitDTO save(CreateMediaKitCommand command, UserDTO user) {
        ArtistEntity artistEntity = artistsRepository.findByUserSlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist with user slug: #" + user.slug()));

        Optional<MediaKitEntity> existingLinkPage = repository.findByArtistUserSlug(artistEntity.getUser().getSlug());
        if (existingLinkPage.isPresent())
            throw new IllegalStateException("Artist with user slug #" + user.slug() + " already has a Media Kit.");

        MediaKitEntity entity = new MediaKitEntity();
        entity.setTitle(command.title());
        entity.setShortBio(command.shortBio());
        entity.setLongBio(command.longBio());
        entity.setContactEmail(command.contactEmail());
        entity.setContactPhone(command.contactPhone());
        entity.setSlug(generateUniqueSlug());
        entity.setArtist(artistEntity);

        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public MediaKitDTO update(UpdateMediaKitCommand command, String slug) {
        MediaKitEntity existingMediaKit = findMediaKitBySlugOrThrow(slug);

        if (command.title() != null && !command.title().isBlank()
                && !command.title().equals(existingMediaKit.getTitle()))
            existingMediaKit.setTitle(command.title());
        if (command.shortBio() != null && !command.shortBio().isBlank()
                && !command.shortBio().equals(existingMediaKit.getShortBio()))
            existingMediaKit.setShortBio(command.shortBio());
        if (command.longBio() != null && !command.longBio().isBlank()
                && !command.longBio().equals(existingMediaKit.getLongBio()))
            existingMediaKit.setLongBio(command.longBio());
        if (command.contactEmail() != null && !command.contactEmail().isBlank()
                && !command.contactEmail().equals(existingMediaKit.getContactEmail()))
            existingMediaKit.setContactEmail(command.contactEmail());
        if (command.contactPhone() != null && !command.contactPhone().isBlank()
                && !command.contactPhone().equals(existingMediaKit.getContactPhone()))
            existingMediaKit.setContactPhone(command.contactPhone());

        return repository.save(existingMediaKit).toDTO();
    }

    @Override
    @Transactional
    public MediaKitDTO findBySlug(String slug) {
        return findMediaKitBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public MediaKitDTO findByArtistSlug(String slug) {
        return findMediaKitByArtistSlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<MediaKitDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        MediaKitEntity mediaKit = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with slug: #" + slug));

        mediaKit.getArtist().setMediaKit(null);
        repository.delete(mediaKit);
    }

    protected MediaKitEntity findMediaKitBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with slug: #" + slug));
    }

    protected MediaKitEntity findMediaKitByArtistSlugOrThrow(String slug) {
        return repository.findByArtistUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with artist slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
