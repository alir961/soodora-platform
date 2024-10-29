package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.playlist.UpdatePlaylistCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.playlist.PlaylistRepository;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

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
    public PlaylistDTO save(CreatePlaylistCommand command, UserDTO user) {
        UserEntity userEntity = userRepository.findBySlug(user.slug())
            .orElseThrow(() -> new UnknownSlugException("Unable to find product with user slug: #" + user.slug()));
        PlaylistEntity entity = new PlaylistEntity();

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setPrivacy(command.privacy());
        entity.setImageUrl(command.imageUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity).toDTO();
    }

    @Override
    public PlaylistDTO update(UpdatePlaylistCommand command, String slug) {
        PlaylistEntity existingPlaylist = findPlaylistBySlugOrThrow(slug);

        if (command.name() != null && !command.name().equals(existingPlaylist.getName()))
            existingPlaylist.setName(command.name());
        if (command.description() != null && !command.description().equals(existingPlaylist.getDescription()))
            existingPlaylist.setDescription(command.description());
        if (command.imageUrl() != null && !command.imageUrl().equals(existingPlaylist.getImageUrl()))
            existingPlaylist.setImageUrl(command.imageUrl());
        if (command.privacyState() != null && !command.privacyState().equals(existingPlaylist.getPrivacy()))
            existingPlaylist.setPrivacy(command.privacyState());

        return repository.save(existingPlaylist).toDTO();
    }

    @Override
    public PlaylistDTO findBySlug(String slug) {
        return findPlaylistBySlugOrThrow(slug).toDTO();
    }

    @Override
    public List<PlaylistDTO> findAll() {
        return repository.findAll().stream()
            .map(PlaylistEntity::toDTO)
            .toList();
    }

    @Override
    public void deletePlaylistBySlug(String slug) {
        PlaylistEntity playlist = repository.findBySlug(slug)
            .orElseThrow(() -> new UnknownSlugException("Unable to find playlist with slug: #" + slug));

        repository.deleteById(playlist.getId());
    }

    @Override
    public boolean isPlaylistNameUsedByUser(String name, String userSlug) {
        UserEntity user = userRepository.findBySlug(userSlug)
            .orElseThrow(() -> new UnknownSlugException("Unable to find playlist of a user with slug: #" + userSlug));

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
