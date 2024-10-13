package com.grooveconductor.application.playlist;

import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.domain.playlist.PlaylistDto;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.playlist.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<PlaylistDto> retrieveAllPlaylists() {
        return playlistRepository.findAll();
    }

    public List<PlaylistDto> retrieveAllPlaylistsOfUserBySlug(String slug) {
        if (slug != null)
            return playlistRepository.findPlaylistsByUserSlug(slug);
        else
           return retrieveAllPlaylists();
    }

    public PlaylistDto retrievePlaylistBySlug(String slug) {
        return playlistRepository.findBySlug(slug);
    }

    public void deletePlaylistBySlug(String slug) throws UnknownPlaylistException {
        try {
            playlistRepository.deletePlaylistBySlug(slug);
        } catch (UnknownPlaylistException exception) {
            throw new UnknownPlaylistException(exception.getMessage());
        }
    }

    public void createPlaylist(CreatePlaylistCommand command, UserDTO user) {
        if (playlistRepository.isPlaylistNameUsedByUser(command.name(), user.slug()))
            throw new PlaylistUnityException("Name is already used");

        playlistRepository.save(new CreatePlaylistCommand(command.name(), command.description(), command.state(), command.imageUrl()), user);
    }

}
