package com.soodora.test.factory;

import com.soodora.domain.mediakit.MediaKit;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.TestConstants.ARTIST_USER;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitFactory {

    public static MediaKit buildDefaultMediaKit() {
        return new MediaKit(
                "slug",
                ARTIST_USER,
                "Title",
                "ShortBio",
                "LongBio",
                "contactEmail",
                "contactPhone",
                new ArrayList<>(),
                0,
                0);
    }

}
