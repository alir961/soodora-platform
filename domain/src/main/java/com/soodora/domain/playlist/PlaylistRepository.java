package com.soodora.domain.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistRepository {

    Playlist save(CreatePlaylistCommand command, User user, FileData playlistImage);
    Playlist update(UpdatePlaylistCommand command, String slug, FileData playlistImage);
    Playlist findBySlug(String slug);
    List<Playlist> findAll();
    Page<Playlist> findByFilter(PlaylistFilter filter, Pageable pageable);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);
    Playlist addPostToPlaylist(Playlist playlist, String postSlug);
    Playlist removePostFromPlaylist(Playlist playlist, String postSlug);
    void deletePlaylistAndUnlinkPosts(String slug);
    Page<Post> findPlaylistPosts(String playlistSlug, Pageable pageable);

}
