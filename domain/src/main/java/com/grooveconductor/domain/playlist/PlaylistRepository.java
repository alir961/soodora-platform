package com.grooveconductor.domain.playlist;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PlaylistRepository {

    PlaylistDTO save(CreatePlaylistCommand command, UserDTO user);
    PlaylistDTO findBySlug(String slug);
    List<PlaylistDTO> findAll();
    void deletePlaylistBySlug(String slug);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);

}
