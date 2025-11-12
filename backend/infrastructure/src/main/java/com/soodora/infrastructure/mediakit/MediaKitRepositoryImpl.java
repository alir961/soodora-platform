package com.soodora.infrastructure.mediakit;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkPageUnityException;
import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakit.MediaKitRepository;
import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class MediaKitRepositoryImpl implements MediaKitRepository {

    private final MediaKitJpaRepository mediaKitRepository;
    private final ArtistJpaRepository artistRepository;

    @Override
    @Transactional
    public MediaKitDTO save(CreateMediaKitCommand command, User user) {
        ArtistEntity artistEntity = artistRepository.findByUserSlug(user.getSlug())
                .orElseThrow(
                        () -> new UnknownSlugException("Unable to find artist with user slug: #" + user.getSlug()));
        boolean artistHasMediaKit = mediaKitRepository.findByArtistUserSlug(artistEntity.getUser().getSlug())
                .isPresent();

        if (!artistHasMediaKit) {
            MediaKitEntity mediaKit = new MediaKitEntity();

            mediaKit.setTitle(command.title());
            mediaKit.setShortBio(command.shortBio());
            mediaKit.setLongBio(command.longBio());
            mediaKit.setContactEmail(command.contactEmail());
            mediaKit.setContactPhone(command.contactPhone());
            mediaKit.setSlug(generateUniqueSlug());
            mediaKit.setArtist(artistEntity);

            return mediaKitRepository.save(mediaKit).aggregate();
        } else
            throw new LinkPageUnityException("Artist with user slug #" + user.getSlug() + " already has a media kit.");
    }

    @Override
    @Transactional
    public MediaKitDTO update(UpdateMediaKitCommand command, String slug) {
        MediaKitEntity mediaKit = findMediaKitBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.title()))
            mediaKit.setTitle(command.title());
        if (isNotNullOrBlank(command.shortBio()))
            mediaKit.setShortBio(command.shortBio());
        if (isNotNullOrBlank(command.longBio()))
            mediaKit.setLongBio(command.longBio());
        if (isNotNullOrBlank(command.contactEmail()))
            mediaKit.setContactEmail(command.contactEmail());
        if (isNotNullOrBlank(command.contactPhone()))
            mediaKit.setContactPhone(command.contactPhone());
        return mediaKitRepository.save(mediaKit).aggregate();
    }

    @Override
    @Transactional
    public MediaKitDTO findBySlug(String slug) {
        return findMediaKitBySlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public MediaKitDTO findByArtistSlug(String slug) {
        return findMediaKitByArtistSlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public List<MediaKitDTO> findAll() {
        return mediaKitRepository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySlug(String slug) {
        MediaKitEntity mediaKit = mediaKitRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with slug: #" + slug));

        mediaKit.getArtist().setMediaKit(null);
        mediaKitRepository.delete(mediaKit);
    }

    protected MediaKitEntity findMediaKitBySlugOrThrow(String slug) {
        return mediaKitRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with slug: #" + slug));
    }

    protected MediaKitEntity findMediaKitByArtistSlugOrThrow(String slug) {
        return mediaKitRepository.findByArtistUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find media kit with artist slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (mediaKitRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
