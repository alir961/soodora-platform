package com.soodora.domain.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistRepository {

    PlaylistDTO save(CreatePlaylistCommand command, User user, FileData playlistImage);
    PlaylistDTO update(UpdatePlaylistCommand command, String slug, FileData playlistImage);
    PlaylistFullDTO findBySlug(String slug, Pageable pageableAudioPosts);
    List<PlaylistDTO> findAll();
    Page<PlaylistDTO> findByFilter(PlaylistFilter filter, Pageable pageable);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);
    PlaylistFullDTO addPostToPlaylist(String playlistSlug, String postSlug, Pageable pageableAudioPosts);
    PlaylistFullDTO removePostFromPlaylist(String playlistSlug, String postSlug, Pageable pageableAudioPosts);
    PlaylistFullDTO getPlaylistWithPosts(String playlistSlug, Pageable pageableAudioPosts);
    void deletePlaylistAndUnlinkPosts(String slug);

}
