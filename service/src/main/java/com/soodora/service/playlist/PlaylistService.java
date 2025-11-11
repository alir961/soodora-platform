package com.soodora.service.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistDTO createPlaylist(CreatePlaylistCommand command, User user, FileData imageFile)
            throws PlaylistUnityException {
        if (playlistRepository.isPlaylistNameUsedByUser(command.name(), user.getSlug()))
            throw new PlaylistUnityException("Name is already used");
        return playlistRepository.save(command, user, imageFile);
    }

    public List<PlaylistDTO> retrieveAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Page<PlaylistDTO> retrievePlaylists(PlaylistFilter filter, Pageable pageable) {
        return playlistRepository.findByFilter(filter, pageable);
    }

    public PopulatedPlaylistDTO retrievePlaylistBySlug(String slug, Pageable pageableAudioPosts) {
        return playlistRepository.findBySlug(slug, pageableAudioPosts);
    }

    public PlaylistDTO updatePlaylist(UpdatePlaylistCommand command, String slug, FileData imageFile)
            throws PlaylistUnityException {
        try {
            return playlistRepository.update(command, slug, imageFile);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

    public void deletePlaylistBySlug(String slug) throws UnknownPlaylistException {
        try {
            playlistRepository.deletePlaylistAndUnlinkPosts(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPlaylistException(exception.getMessage());
        }
    }

    public PopulatedPlaylistDTO addPostToPlaylist(String playlistSlug, String postSlug, Pageable pageableAudioPosts) {
        try {
            return playlistRepository.addPostToPlaylist(playlistSlug, postSlug, pageableAudioPosts);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

    public PopulatedPlaylistDTO removePostFromPlaylist(String playlistSlug, String postSlug,
            Pageable pageableAudioPosts) {
        try {
            return playlistRepository.removePostFromPlaylist(playlistSlug, postSlug, pageableAudioPosts);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

}
