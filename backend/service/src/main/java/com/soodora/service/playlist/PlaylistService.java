package com.soodora.service.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public Playlist createPlaylist(User user, CreatePlaylistCommand command, FileData imageFile)
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

    public Playlist retrievePlaylistBySlug(User user, String playlistSlug) {
        try {
            Playlist playlist = playlistRepository.findBySlug(playlistSlug);

            if (playlist.getPrivacy() == PUBLIC || playlist.hasAccess(user))
                return playlist;
            throw new InvalidPermissionException("Only the playlist owner can access the playlist");
        } catch (UnknownSlugException exception) {
            throw new UnknownPlaylistException(exception.getMessage());
        }
    }

    public Page<Post> retrievePlaylistPostsBySlug(User user, String playlistSlug, Pageable pageable) {
        Playlist playlist = retrievePlaylistBySlug(user, playlistSlug);

        if (playlist.getPrivacy() == PUBLIC || playlist.hasAccess(user))
            return playlistRepository.findPlaylistPosts(playlistSlug, pageable);
        throw new InvalidPermissionException("Only the playlist owner can access the playlist posts");
    }

    public Playlist updatePlaylist(User user, String playlistSlug, UpdatePlaylistCommand command, FileData imageFile)
            throws PlaylistUnityException {
        Playlist playlist = retrievePlaylistBySlug(user, playlistSlug);

        if (!playlist.hasAccess(user))
            throw new InvalidPermissionException("Only the playlist owner can update the playlist");
        if (playlistRepository.isPlaylistNameUsedByUser(command.name(), user.getSlug()))
            throw new PlaylistUnityException("Playlist name is already used");
        return playlistRepository.update(command, playlistSlug, imageFile);
    }

    public void deletePlaylist(User user, String playlistSlug) throws UnknownPlaylistException {
        Playlist playlist = retrievePlaylistBySlug(user, playlistSlug);

        if (!playlist.hasAccess(user))
            throw new InvalidPermissionException("Only the playlist owner can delete the playlist");
        playlistRepository.deletePlaylistAndUnlinkPosts(playlistSlug);
    }

    public Playlist addPostToPlaylist(User user, String playlistSlug, String postSlug) {
        Playlist playlist = retrievePlaylistBySlug(user, playlistSlug);

        if (!playlist.hasAccess(user))
            throw new InvalidPermissionException("Only the playlist owner can add a post to the playlist");
        try {
            return playlistRepository.addPostToPlaylist(playlist, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Playlist removePostFromPlaylist(User user, String playlistSlug, String postSlug) {

        Playlist playlist = retrievePlaylistBySlug(user, playlistSlug);

        if (!playlist.hasAccess(user))
            throw new InvalidPermissionException("Only the playlist owner can delete a post from the playlist");
        try {
            return playlistRepository.removePostFromPlaylist(playlist, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }

    }

}
