package com.soodora.domain.mediakit;

import com.soodora.domain.user.User;

import java.util.List;

public interface MediaKitRepository {

    MediaKit save(User user, CreateMediaKitCommand command);

    List<MediaKit> findAll();
    MediaKit find(String mediaKitSlug);

    MediaKit update(String mediaKitSlug, UpdateMediaKitCommand command);

    void delete(String mediaKitSlug);

    void persistDownload(User user, String slug);

    MediaKit findByArtistSlug(String artistSlug);

}
