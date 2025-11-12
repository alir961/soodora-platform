package com.soodora.domain.mediakititem;

import com.soodora.domain.cdn.FileData;

public interface MediaKitItemRepository {

    MediaKitItem save(CreateMediaKitItemCommand command, String artistSlug, FileData fileData);

    MediaKitItem update(UpdateMediaKitItemCommand command, String slug, FileData fileData);

    void deleteBySlug(String slug);

}
