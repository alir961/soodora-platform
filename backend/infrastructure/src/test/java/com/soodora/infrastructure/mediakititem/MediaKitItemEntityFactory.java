package com.soodora.infrastructure.mediakititem;

import com.soodora.infrastructure.mediakit.MediaKitEntity;

import java.util.Date;

import static java.util.UUID.randomUUID;

public class MediaKitItemEntityFactory {

    public static MediaKitItemEntity buildDefaultMediaKitEntity(MediaKitEntity mediaKit) {
        return new MediaKitItemEntity(
                randomUUID(),
                "slug",
                mediaKit,
                "title",
                "shortBio",
                "longBio",
                new Date());
    }

}
