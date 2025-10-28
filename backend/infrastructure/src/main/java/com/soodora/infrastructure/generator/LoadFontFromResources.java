package com.soodora.infrastructure.generator;

import com.soodora.domain.mediakit.MediaKitGenerationException;

import java.io.InputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LoadFontFromResources {

    protected Font loadFontFromResources(String resourcePath, int size, int style, BaseColor color) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null)
                throw new MediaKitGenerationException("Font not found: " + resourcePath);

            byte[] fontBytes = inputStream.readAllBytes();

            BaseFont baseFont = BaseFont.createFont(
                    resourcePath,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontBytes,
                    null);

            return new Font(baseFont, size, style, color);
        } catch (Exception e) {
            log.error("Unable to load font {}: {}", resourcePath, e.getMessage());
            throw new MediaKitGenerationException("Unable to load font " + resourcePath);
        }
    }

}
