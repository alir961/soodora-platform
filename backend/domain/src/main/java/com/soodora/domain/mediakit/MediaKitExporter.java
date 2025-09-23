package com.soodora.domain.mediakit;

import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface MediaKitExporter {

    ByteArrayOutputStream exportMediaKit(MediaKitDTO mediaKit, List<MediaKitItemDTO> items)
            throws MediaKitGenerationException;

}
