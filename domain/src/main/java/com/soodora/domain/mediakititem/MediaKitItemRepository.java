package com.soodora.domain.mediakititem;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.mediakit.MediaKit;

public interface MediaKitItemRepository {

    MediaKitItem save(CreateMediaKitItemCommand command, String artistSlug, FileData fileData);

    MediaKit findMediaKitByItemSlug(String slug);

    MediaKitItem update(String mediaKitSlug, UpdateMediaKitItemCommand command, FileData fileData);

    void delete(String slug);

}
