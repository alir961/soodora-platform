package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.CreatePlaylistCommand;
import com.grooveconductor.domain.dto.PlaylistDto;
import com.grooveconductor.domain.dto.UserDTO;

import java.util.List;

public interface PlaylistRepository {

    PlaylistDto save(CreatePlaylistCommand command, UserDTO user);
    PlaylistDto findBySlug(String slug);
    List<PlaylistDto> findPlaylistsByUserSlug(String userSlug);
    List<PlaylistDto> findAll();
    void deletePlaylistBySlug(String slug);
    boolean isPlaylistNameUsedByUser(String name, String userSlug);

}
