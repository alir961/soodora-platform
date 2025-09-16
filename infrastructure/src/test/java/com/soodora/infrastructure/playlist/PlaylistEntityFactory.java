package com.soodora.infrastructure.playlist;

import com.soodora.infrastructure.user.UserEntity;
import com.soodora.test.factory.PlaylistFactory;

import java.util.Date;

import static java.util.UUID.randomUUID;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;

public class PlaylistEntityFactory extends PlaylistFactory {

    public static PlaylistEntity buildDefaultPlaylistEntity(UserEntity user) {
        return new PlaylistEntity(
                randomUUID(),
                "slug",
                "Name",
                "Description",
                PUBLIC,
                "imageUrl",
                user,
                null,
                new Date(),
                new Date());
    }

}
