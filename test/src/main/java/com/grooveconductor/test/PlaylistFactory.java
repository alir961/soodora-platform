package com.grooveconductor.test;

import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.playlist.PlaylistDto;

public class PlaylistFactory {

    public static PlaylistDto buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDto(slug, name, "Default Description", PlaylistPrivacy.PUBLIC, "image-url", "userSlug");
    }

}