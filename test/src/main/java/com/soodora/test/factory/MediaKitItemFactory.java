package com.soodora.test.factory;

import com.soodora.domain.mediakititem.MediaKitItem;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.TestConstants.MEDIA_KIT;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitItemFactory {

    public static MediaKitItem buildDefaultMediaKitItem() {
        return new MediaKitItem(
                MEDIA_KIT,
                "item-slug",
                "type",
                "Title",
                "Content");
    }

}
