package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.test.PlaylistFactory;

import java.util.Date;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;
import static java.util.UUID.randomUUID;

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
                new Date(),
                new Date()
        );
    }

}
