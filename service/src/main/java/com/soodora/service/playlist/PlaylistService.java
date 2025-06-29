package com.soodora.service.playlist;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public void createPlaylist(CreatePlaylistCommand command, UserDTO user) throws PlaylistUnityException {
        if (playlistRepository.isPlaylistNameUsedByUser(command.name(), user.slug()))
            throw new PlaylistUnityException("Name is already used");
        playlistRepository.save(command, user);
    }

    public List<PlaylistDTO> retrieveAllPlaylists() {
        return playlistRepository.findAll();
    }

    public List<PlaylistDTO> retrievePlaylists(PlaylistFilter filter) {
        if (filter.userSlug().isPresent())
            return userRepository.retrievePlaylists(createFromSlug(filter.userSlug().get()));
        return retrieveAllPlaylists();
    }

    public PlaylistDTO retrievePlaylistBySlug(String slug) {
        return playlistRepository.findBySlug(slug);
    }

    public PlaylistDTO updatePlaylist(UpdatePlaylistCommand command, String slug) {
        try {
            return playlistRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

    public void deletePlaylistBySlug(String slug) throws UnknownPlaylistException {
        try {
            playlistRepository.deletePlaylistBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPlaylistException(exception.getMessage());
        }
    }

}
