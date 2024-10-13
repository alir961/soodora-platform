package com.grooveconductor.test;

import com.grooveconductor.domain.PlaylistPrivacy;
import com.grooveconductor.domain.dto.PlaylistDto;

public class PlaylistFactory {

    public static PlaylistDto buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDto(slug, name, "Default Description", PlaylistPrivacy.PUBLIC, "image-url", "userSlug");
    }

}