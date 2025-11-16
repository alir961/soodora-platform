package com.soodora.service.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.post.Post;
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

    public Playlist createPlaylist(CreatePlaylistCommand command, User user, FileData imageFile)
            throws PlaylistUnityException {
        if (playlistRepository.isPlaylistNameUsedByUser(command.name(), user.getSlug()))
            throw new PlaylistUnityException("Name is already used");
        return playlistRepository.save(command, user, imageFile);
    }

    public List<Playlist> retrieveAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Page<Playlist> retrievePlaylists(PlaylistFilter filter, Pageable pageable) {
        return playlistRepository.findByFilter(filter, pageable);
    }

    public Playlist retrievePlaylistBySlug(String slug) {
        return playlistRepository.findBySlug(slug);
    }

    public Page<Post> retrievePlaylistPostsBySlug(String playlistSlug, Pageable pageable) {
        return playlistRepository.findPlaylistPosts(playlistSlug, pageable);
    }

    public Playlist updatePlaylist(UpdatePlaylistCommand command, String slug, FileData imageFile)
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

    public Playlist addPostToPlaylist(String playlistSlug, String postSlug) {
        try {
            return playlistRepository.addPostToPlaylist(playlistSlug, postSlug);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

    public Playlist removePostFromPlaylist(String playlistSlug, String postSlug) {
        try {
            return playlistRepository.removePostFromPlaylist(playlistSlug, postSlug);
        } catch (UnknownSlugException exception) {
            throw new PlaylistUnityException(exception.getMessage());
        }
    }

}
