package com.soodora.api.mediakit;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakit.MediaKit;

import java.util.List;

public record MediaKitDTO(String slug, String artistName, String title, String shortBio, String longBio,
        String contactEmail, String contactPhone,
        List<MediaKitItemDTO> items) implements JsonResponse {

    public static MediaKitDTO build(MediaKit mediaKit) {
        return new MediaKitDTO(
                mediaKit.getSlug(),
                mediaKit.getOwner().getUsername().value(),
                mediaKit.getTitle(),
                mediaKit.getShortBio(),
                mediaKit.getLongBio(),
                mediaKit.getContactEmail(),
                mediaKit.getContactPhone(),
                mediaKit.getItems().stream()
                        .map(MediaKitItemDTO::build)
                        .toList());
    }
}
