package com.soodora.infrastructure.mediakit;

import com.soodora.infrastructure.artist.ArtistEntity;

import java.util.ArrayList;
import java.util.Date;

import static java.util.UUID.randomUUID;

public class MediaKitEntityFactory {

    public static MediaKitEntity buildDefaultMediaKitEntity(ArtistEntity artist) {
        return new MediaKitEntity(
                randomUUID(),
                "slug",
                artist,
                "title",
                "shortBio",
                "longBio",
                "contactEmail",
                "contactPhone",
                new Date(),
                new Date(),
                new ArrayList<>());
    }

}
