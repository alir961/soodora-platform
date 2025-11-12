package com.soodora.domain.mediakititem;

import com.soodora.domain.cdn.FileData;

import java.util.List;

public interface MediaKitItemRepository {

    MediaKitItemDTO save(CreateMediaKitItemCommand command, String artistSlug, FileData fileData);
    MediaKitItemDTO update(UpdateMediaKitItemCommand command, String slug, FileData fileData);
    MediaKitItemDTO findBySlug(String slug);
    List<MediaKitItemDTO> findAll();
    void deleteBySlug(String slug);

}
