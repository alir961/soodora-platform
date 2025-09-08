package com.soodora.test.factory;

import com.soodora.domain.playlist.PlaylistDTO;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static PlaylistDTO buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDTO(
                slug,
                name,
                "Description",
                0,
                PUBLIC,
                "imageUrl",
                buildDefaultUser());
    }

}
