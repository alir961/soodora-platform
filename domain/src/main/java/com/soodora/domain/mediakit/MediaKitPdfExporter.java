package com.soodora.domain.mediakit;

import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface MediaKitPdfExporter {
    ByteArrayOutputStream generateMediaKitPdf(MediaKitDTO mediaKit, List<MediaKitItemDTO> items)
            throws Exception;
}
