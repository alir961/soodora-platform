package com.soodora.domain.mediakit;

import com.soodora.domain.user.User;

import java.util.List;

public interface MediaKitRepository {

    MediaKitDTO save(CreateMediaKitCommand command, User user);
    MediaKitDTO update(UpdateMediaKitCommand command, String slug);
    MediaKitDTO findBySlug(String slug);
    MediaKitDTO findByArtistSlug(String slug);
    List<MediaKitDTO> findAll();
    void deleteBySlug(String slug);

}
