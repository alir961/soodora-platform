package com.soodora.test.factory;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.playlist.Playlist;

import java.util.Date;
import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PostFactory.buildDefaultPost;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static Playlist buildDefaultPlaylist(String slug, String name) {
        return new Playlist(
                slug,
                name,
                "Description",
                PUBLIC,
                new Url("https://soodora.com/imageUrl.png"),
                USER,
                List.of(
                        buildDefaultPost("postSlug1", "Post 1"),
                        buildDefaultPost("postSlug2", "Post 2"),
                        buildDefaultPost("postSlug3", "Post 3")),
                new Date(),
                new Date());
    }

}
