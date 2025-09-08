package com.soodora.infrastructure.cloudinary;

import com.soodora.domain.cdn.CloudinaryUploadFailedException;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryImageStorageUploader implements ImageStorageUploader {

    private final Cloudinary cloudinary;

    public CloudinaryImageStorageUploader(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(FileData fileData) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    fileData.content(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "public_id", fileData.filename()));
            return uploadResult.get("secure_url").toString();
        } catch (IOException error) {
            throw new CloudinaryUploadFailedException("Upload failed : " + error.getMessage());
        }
    }
}
