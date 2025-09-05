package com.soodora.domain.cdn;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageUploader {
    String upload(MultipartFile file);
}
