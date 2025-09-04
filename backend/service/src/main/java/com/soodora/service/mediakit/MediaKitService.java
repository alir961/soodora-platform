package com.soodora.service.mediakit;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaKitService {

    private final MediaKitRepository mediaKitRepository;
    private final MediaKitItemRepository mediaKitItemRepository;
    private final MediaKitExporter mediaKitExporter;

    public MediaKitDTO createMediaKit(CreateMediaKitCommand command, UserDTO user) {
        return mediaKitRepository.save(command, user);
    }

    public MediaKitDTO retrieveMediaKitBySlug(String slug) {
        try {
            return mediaKitRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKitDTO retrieveMediaKitByArtistSlug(String slug) {
        try {
            return mediaKitRepository.findByArtistSlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKitDTO updateMediaKit(UpdateMediaKitCommand command, String slug) {
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

    public MediaKitItemDTO addMediaKitItem(CreateMediaKitItemCommand command, String artistSlug) {
        try {
            return mediaKitItemRepository.save(command, artistSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public MediaKitItemDTO updateMediaKitItem(UpdateMediaKitItemCommand command, String slug) {
        try {
            return mediaKitItemRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public void deleteMediaKitItem(String slug) {
        try {
            mediaKitItemRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownMediaKitException(exception.getMessage());
        }
    }

    public byte[] exportMediaKitToPdf(String slug) {
        try {
            MediaKitDTO mediaKit = retrieveMediaKitBySlug(slug);
            List<MediaKitItemDTO> items = mediaKitItemRepository.findAll()
                    .stream()
                    .filter(i -> i.mediaKitSlug().equals(slug))
                    .toList();

            return mediaKitExporter.exportMediaKit(mediaKit, items)
                    .toByteArray();
        } catch (MediaKitGenerationException e) {
            throw new InvalidRequestException("Failed to export MediaKit to PDF: " + e.getMessage());
        }
    }

}
