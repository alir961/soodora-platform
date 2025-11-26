package com.soodora.domain.mediakit;

import com.soodora.domain.user.User;

import java.util.List;

public interface MediaKitRepository {

    MediaKit save(CreateMediaKitCommand command, User user);
    MediaKit update(UpdateMediaKitCommand command, String slug);
    MediaKit findBySlug(String slug);
    MediaKit findByArtistSlug(String slug);
    List<MediaKit> findAll();
    void deleteBySlug(String slug);

    void persistDownload(String slug, User user);

}
