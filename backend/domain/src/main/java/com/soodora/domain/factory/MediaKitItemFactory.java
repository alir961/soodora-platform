package com.soodora.domain.factory;

import com.soodora.domain.mediakititem.MediaKitItem;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.factory.MediaKitFactory.buildDefaultMediaKit;

@NoArgsConstructor(access = PROTECTED)
public class MediaKitItemFactory {

    public static MediaKitItem buildDefaultMediaKitItem() {
        return new MediaKitItem(
                buildDefaultMediaKit().getSlug(),
                "item-slug",
                "type",
                "Title",
                "Content");
    }

}
