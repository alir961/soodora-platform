package com.soodora.domain.mediakit;

import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.util.List;

public record MediaKitDTO(String slug, String title, String shortBio, String longBio,
        String contactEmail, String contactPhone,
        List<MediaKitItemDTO> items) {
}
