package com.soodora.infrastructure.cloudinary;

import com.soodora.domain.cdn.CloudinaryUploadFailedException;
import com.soodora.domain.cdn.ImageStorageUploader;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryImageStorageComponent implements ImageStorageUploader {

    private final Cloudinary cloudinary;

    public CloudinaryImageStorageComponent(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException error) {
            throw new CloudinaryUploadFailedException("Upload failed : " + error.getMessage());
        }
    }

}
