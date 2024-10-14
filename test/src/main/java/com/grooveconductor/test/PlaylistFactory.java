package com.grooveconductor.test;

import com.grooveconductor.domain.playlist.PlaylistDto;
import lombok.NoArgsConstructor;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static PlaylistDto buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDto(
                slug,
                name,
                "Default Description",
                PUBLIC,
                "imageUrl",
                "userSlug"
        );
    }

}