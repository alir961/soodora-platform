package com.soodora.api.mediakit;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakititem.MediaKitItem;

public record MediaKitItemDTO(String mediaKitSlug, String slug, String type, String title,
        String content) implements JsonResponse {

    public static MediaKitItemDTO build(MediaKitItem mediaKitItem) {
        return new MediaKitItemDTO(
                mediaKitItem.getMediaKitSlug(),
                mediaKitItem.getSlug(),
                mediaKitItem.getType(),
                mediaKitItem.getTitle(),
                mediaKitItem.getContent());
    }

}
