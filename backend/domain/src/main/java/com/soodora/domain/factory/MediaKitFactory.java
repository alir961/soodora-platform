package com.soodora.domain.factory;

import com.soodora.domain.mediakit.MediaKit;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.factory.UserFactory.buildDefaultArtistUser;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitFactory {

    public static MediaKit buildDefaultMediaKit() {
        return new MediaKit(
                "slug",
                buildDefaultArtistUser(),
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
