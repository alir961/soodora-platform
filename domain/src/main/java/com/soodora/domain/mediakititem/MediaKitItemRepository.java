package com.soodora.domain.mediakititem;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MediaKitItemRepository {

    MediaKitItemDTO save(CreateMediaKitItemCommand command, String artistSlug, MultipartFile imageFile);
    MediaKitItemDTO update(UpdateMediaKitItemCommand command, String slug, MultipartFile imageFile);
    MediaKitItemDTO findBySlug(String slug);
    List<MediaKitItemDTO> findAll();
    void deleteBySlug(String slug);

}
