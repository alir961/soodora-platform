package com.soodora.test.factory;

import com.soodora.domain.mediakit.MediaKitDTO;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitFactory {

    public static MediaKitDTO buildMediaKitDTO() {
        return new MediaKitDTO(
                "slug",
                "artist",
                "title",
                "shortBio",
                "longBio",
                "contactEmail",
                "contactPhone",
                new ArrayList<>());
    }

}
