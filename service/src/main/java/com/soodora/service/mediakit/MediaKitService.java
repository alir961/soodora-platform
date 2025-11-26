package com.soodora.service.mediakit;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.mediakititem.MediaKitItemRepository;
import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.domain.user.User;
import com.soodora.service.common.DocumentGenerationException;
import com.soodora.service.shared.InvalidPermissionException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaKitService {

    private final MediaKitRepository mediaKitRepository;
    private final MediaKitItemRepository mediaKitItemRepository;
    private final PdfExporter pdfExporter;

    public MediaKit createMediaKit(User user, CreateMediaKitCommand command) {
        return mediaKitRepository.save(user, command);
    }

    public MediaKit retrieveMediaKitBySlug(String mediaKitSlug) {
        try {
            return mediaKitRepository.find(mediaKitSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKit retrieveMediaKitByArtistSlug(String artistSlug) {
        try {
            return mediaKitRepository.findByArtistSlug(artistSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKit updateMediaKit(User user, String mediaKitSlug, UpdateMediaKitCommand command) {
        MediaKit mediaKit = retrieveMediaKitBySlug(mediaKitSlug);

        if (mediaKit.hasAccess(user))
            return mediaKitRepository.update(mediaKitSlug, command);
        throw new InvalidPermissionException("Only the owner can update the Soodora Kit");
    }

    public void deleteMediaKit(User user, String mediaKitSlug) {
        MediaKit mediaKit = retrieveMediaKitBySlug(mediaKitSlug);

        if (!mediaKit.hasAccess(user))
            throw new InvalidPermissionException("Only the owner can delete the Soodora Kit");
        mediaKitRepository.delete(mediaKitSlug);
    }

    public MediaKitItem addMediaKitItem(User user, String artistSlug, CreateMediaKitItemCommand command,
            FileData fileData) {
        MediaKit mediaKit = retrieveMediaKitByArtistSlug(artistSlug);

        if (!mediaKit.hasAccess(user))
            throw new InvalidPermissionException("Only the owner can add items to the Soodora Kit");
        return mediaKitItemRepository.save(command, artistSlug, fileData);
    }

    public MediaKitItem updateMediaKitItem(User user, String mediaKitItemSlug, UpdateMediaKitItemCommand command,
            FileData fileData) {
        try {
            MediaKit mediaKit = mediaKitItemRepository.findMediaKitByItemSlug(mediaKitItemSlug);

            if (!mediaKit.hasAccess(user))
                throw new InvalidPermissionException("Only the owner can update items from the Soodora Kit");
            return mediaKitItemRepository.update(mediaKitItemSlug, command, fileData);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitItemException(exception.getMessage());
        }
    }

    public void deleteMediaKitItem(User user, String mediaKitItemSlug) {
        try {
            MediaKit mediaKit = mediaKitItemRepository.findMediaKitByItemSlug(mediaKitItemSlug);

            if (!mediaKit.hasAccess(user))
                throw new InvalidPermissionException("Only the owner can delete items from the Soodora Kit");
            mediaKitItemRepository.delete(mediaKitItemSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitItemException(exception.getMessage());
        }
    }

    public byte[] exportMediaKitToPdf(User user, String mediaKitSlug) {
        try {
            MediaKit mediaKit = retrieveMediaKitBySlug(mediaKitSlug);
            byte[] file = pdfExporter.exportMediaKit(mediaKit).toByteArray();

            mediaKitRepository.persistDownload(user, mediaKitSlug);
            return file;
        } catch (MediaKitGenerationException exception) {
            throw new DocumentGenerationException("Failed to export Soodora Kit to PDF: " + exception.getMessage());
        }
    }

}
