package com.soodora.test.factory;

import com.soodora.domain.mediakititem.MediaKitItemDTO;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitItemFactory {

    public static MediaKitItemDTO buildMediaKitItemDTO() {
        return new MediaKitItemDTO(
                "mediaKitSlug",
                "Slug",
                "type",
                "title",
                "Image");
    }

}
