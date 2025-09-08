package com.soodora.domain.cdn;

public class CloudinaryUploadFailedException extends RuntimeException {
    public CloudinaryUploadFailedException(String message) {
        super(message);
    }
}
