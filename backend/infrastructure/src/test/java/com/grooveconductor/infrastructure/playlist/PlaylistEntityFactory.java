package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.test.factory.PlaylistFactory;

import java.util.Date;

import static java.util.UUID.randomUUID;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;

public class PlaylistEntityFactory extends PlaylistFactory {

    public static PlaylistEntity buildDefaultPlaylistEntity(UserEntity user) {
        return new PlaylistEntity(
                randomUUID(),
                "slug",
                "Name",
                "Description",
                PUBLIC,
                "imageUrl",
                0,
                user,
                new Date(),
                new Date());
    }

}
