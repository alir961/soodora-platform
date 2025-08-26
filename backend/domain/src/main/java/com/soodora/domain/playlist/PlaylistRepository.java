package com.soodora.domain.playlist;

import com.soodora.domain.user.UserDTO;

import java.util.List;

public interface PlaylistRepository {

    PlaylistDTO save(CreatePlaylistCommand command, UserDTO user);
    PlaylistDTO update(UpdatePlaylistCommand command, String slug);
    PlaylistDTO findBySlug(String slug);
    List<PlaylistDTO> findAll();
    void deletePlaylistBySlug(String slug);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);

}
