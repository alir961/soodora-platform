package com.grooveconductor.test;

import com.grooveconductor.domain.playlist.PlaylistDTO;
import lombok.NoArgsConstructor;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static PlaylistDTO buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDTO(
            slug,
            name,
            "Description",
            PUBLIC,
            "imageUrl",
            buildDefaultUser()
        );
    }

}