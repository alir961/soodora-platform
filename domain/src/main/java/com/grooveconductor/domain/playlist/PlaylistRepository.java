package com.grooveconductor.domain.playlist;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PlaylistRepository {

    PlaylistDto save(CreatePlaylistCommand command, UserDTO user);
    PlaylistDto findBySlug(String slug);
    List<PlaylistDto> findPlaylistsByUserSlug(String userSlug);
    List<PlaylistDto> findAll();
    void deletePlaylistBySlug(String slug);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);

}
