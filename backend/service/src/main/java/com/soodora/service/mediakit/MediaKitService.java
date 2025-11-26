package com.soodora.service.mediakit;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaKitService {

    private final MediaKitRepository mediaKitRepository;
    private final MediaKitItemRepository mediaKitItemRepository;
    private final PdfExporter pdfExporter;

    public MediaKit createMediaKit(CreateMediaKitCommand command, User user) {
        return mediaKitRepository.save(command, user);
    }

    public MediaKit retrieveMediaKitBySlug(String slug) {
        try {
            return mediaKitRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKit retrieveMediaKitByArtistSlug(String slug) {
        try {
            return mediaKitRepository.findByArtistSlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKit updateMediaKit(UpdateMediaKitCommand command, String slug) {
        try {
            return mediaKitRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public void deleteMediaKit(String slug) {
        try {
            mediaKitRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKitItem addMediaKitItem(CreateMediaKitItemCommand command, String artistSlug, FileData fileData) {
        try {
            return mediaKitItemRepository.save(command, artistSlug, fileData);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKitItem updateMediaKitItem(UpdateMediaKitItemCommand command, String slug, FileData fileData) {
        try {
            return mediaKitItemRepository.update(command, slug, fileData);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitItemException(exception.getMessage());
        }
    }

    public void deleteMediaKitItem(String slug) {
        try {
            mediaKitItemRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitItemException(exception.getMessage());
        }
    }

    public byte[] exportMediaKitToPdf(String slug, User user) {
        try {
            MediaKit mediaKit = retrieveMediaKitBySlug(slug);
            byte[] file = pdfExporter.exportMediaKit(mediaKit).toByteArray();

            mediaKitRepository.persistDownload(slug, user);
            return file;
        } catch (MediaKitGenerationException exception) {
            // TODO: it's an internal error not an invalid request
            throw new InvalidRequestException("Failed to export MediaKit to PDF: " + exception.getMessage());
        }
    }

}
