package com.soodora.service.shared;

import com.soodora.domain.cdn.FileData;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class FileDataService {

    public FileData toFileData(MultipartFile file) {
        if (file == null || file.isEmpty())
            return null;
        try {
            return new FileData(file.getBytes(), file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            throw new InvalidMultipartRequestException("Unable to read file: " + file.getOriginalFilename(), e);
        }
    }

}
