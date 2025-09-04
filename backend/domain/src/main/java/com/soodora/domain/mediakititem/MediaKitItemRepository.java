package com.soodora.domain.mediakititem;

import java.util.List;

public interface MediaKitItemRepository {

    MediaKitItemDTO save(CreateMediaKitItemCommand command, String artistSlug);
    MediaKitItemDTO update(UpdateMediaKitItemCommand command, String slug);
    MediaKitItemDTO findBySlug(String slug);
    List<MediaKitItemDTO> findAll();
    void deleteBySlug(String slug);

}
