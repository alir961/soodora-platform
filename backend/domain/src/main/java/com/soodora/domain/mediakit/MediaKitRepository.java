package com.soodora.domain.mediakit;

import com.soodora.domain.user.UserDTO;

import java.util.List;

public interface MediaKitRepository {

    MediaKitDTO save(CreateMediaKitCommand command, UserDTO user);
    MediaKitDTO update(UpdateMediaKitCommand command, String slug);
    MediaKitDTO findBySlug(String slug);
    MediaKitDTO findByArtistSlug(String slug);
    List<MediaKitDTO> findAll();
    void deleteBySlug(String slug);

}
