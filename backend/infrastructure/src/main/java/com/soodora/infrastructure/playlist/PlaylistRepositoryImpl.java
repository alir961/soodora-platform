package com.soodora.infrastructure.playlist;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final PlaylistJpaRepository repository;
    private final UserJpaRepository userRepository;

    protected PlaylistEntity findPlaylistBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find playlist with slug: #" + slug));
    }

    @Override
    @Transactional
    public PlaylistDTO save(CreatePlaylistCommand command, UserDTO user) {
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with user slug: #" + user.slug()));
        PlaylistEntity entity = new PlaylistEntity();

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setNumberOfTracks(command.numberOfTracks());
        entity.setPrivacy(command.privacy());
        entity.setImageUrl(command.imageUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public PlaylistDTO update(UpdatePlaylistCommand command, String slug) {
        PlaylistEntity existingPlaylist = findPlaylistBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.name()))
            existingPlaylist.setName(command.name());
        if (isNotNullOrBlank(command.description()))
            existingPlaylist.setDescription(command.description());
        if (isNotNullOrBlank(command.imageUrl()))
            existingPlaylist.setImageUrl(command.imageUrl());
        if (command.privacyState() != null)
            existingPlaylist.setPrivacy(command.privacyState());
        if (command.numberOfTracks() >= 0)
            existingPlaylist.setNumberOfTracks(command.numberOfTracks());
        return repository.save(existingPlaylist).toDTO();
    }

    @Override
    @Transactional
    public PlaylistDTO findBySlug(String slug) {
        return findPlaylistBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<PlaylistDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deletePlaylistBySlug(String slug) {
        PlaylistEntity playlist = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find playlist with slug: #" + slug));

        repository.deleteById(playlist.getId());
    }

    @Override
    @Transactional
    public boolean isPlaylistNameUsedByUser(String name, String userSlug) {
        UserEntity user = userRepository.findBySlug(userSlug)
                .orElseThrow(
                        () -> new UnknownSlugException("Unable to find playlist of a user with slug: #" + userSlug));

        return user.getPlaylists().stream()
                .anyMatch(playlist -> playlist.getName().equalsIgnoreCase(name));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
