package com.soodora.domain.factory;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.playlist.Playlist;

import java.util.Date;
import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.factory.PostFactory.buildDefaultPost;
import static com.soodora.domain.factory.UserFactory.buildDefaultArtistUser;
import static com.soodora.domain.playlist.PlaylistPrivacy.PRIVATE;
import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static Playlist buildDefaultPublicPlaylist(String slug, String name) {
        return new Playlist(
                slug,
                name,
                "Description",
                PUBLIC,
                new Url("https://soodora.com/imageUrl.png"),
                buildDefaultArtistUser(),
                List.of(
                        buildDefaultPost("postSlug1", "Post 1"),
                        buildDefaultPost("postSlug2", "Post 2"),
                        buildDefaultPost("postSlug3", "Post 3")),
                new Date(),
                new Date());
    }

    public static Playlist buildDefaultPrivatePlaylist(String slug, String name) {
        return new Playlist(
                slug,
                name,
                "Description",
                PRIVATE,
                new Url("https://soodora.com/imageUrl.png"),
                buildDefaultArtistUser(),
                List.of(
                        buildDefaultPost("postSlug1", "Post 1"),
                        buildDefaultPost("postSlug2", "Post 2"),
                        buildDefaultPost("postSlug3", "Post 3")),
                new Date(),
                new Date());
    }

}
