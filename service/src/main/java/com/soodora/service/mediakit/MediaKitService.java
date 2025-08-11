package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.shared.PdfGeneratorService;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaKitService {

    private final MediaKitRepository mediaKitRepository;
    private final MediaKitItemRepository mediaKitItemRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public MediaKitDTO createMediaKit(CreateMediaKitCommand command, UserDTO user) {
        return mediaKitRepository.save(command, user);
    }

    public MediaKitDTO retrieveMediaKitBySlug(String slug) {
        try {
            return mediaKitRepository.findBySlug(slug);
        } catch (Exception e) {
            throw new UnknownMediaKitException(e.getMessage());
        }
    }

    public MediaKitDTO retrieveMediaKitByArtistSlug(String slug) {
        try {
            return mediaKitRepository.findByArtistSlug(slug);
        } catch (Exception e) {
            throw new UnknownMediaKitException(e.getMessage());
        }
    }

    public MediaKitDTO updateMediaKit(UpdateMediaKitCommand command, String slug) {
        try {
            return mediaKitRepository.update(command, slug);
        } catch (Exception e) {
            throw new UnknownMediaKitException("Failed to update MediaKit: " + e.getMessage());
        }
    }

    public void deleteMediaKit(String slug) {
        try {
            mediaKitRepository.deleteBySlug(slug);
        } catch (Exception e) {
            throw new UnknownMediaKitException("Failed to delete MediaKit: " + e.getMessage());
        }
    }

    public MediaKitItemDTO addMediaKitItem(CreateMediaKitItemCommand command, String artistSlug) {
        try {
            return mediaKitItemRepository.save(command, artistSlug);
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to add MediaKit item: " + e.getMessage());
        }
    }

    public MediaKitItemDTO updateMediaKitItem(UpdateMediaKitItemCommand command, String slug) {
        try {
            return mediaKitItemRepository.update(command, slug);
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to update MediaKit item: " + e.getMessage());
        }
    }

    public void deleteMediaKitItem(String slug) {
        try {
            mediaKitItemRepository.deleteBySlug(slug);
        } catch (Exception e) {
            throw new UnknownMediaKitItemException(e.getMessage());
        }
    }

    public byte[] exportMediaKitToPdf(String slug) {
        try {
            MediaKitDTO mediaKit = mediaKitRepository.findBySlug(slug);
            if (mediaKit == null)
                throw new InvalidRequestException("MediaKit with slug '" + slug + "' not found for export.");

            List<MediaKitItemDTO> items = mediaKitItemRepository.findAll()
                    .stream()
                    .filter(i -> i.mediaKitSlug().equals(slug))
                    .toList();

            ByteArrayOutputStream out = pdfGeneratorService.generateMediaKitPdf(mediaKit, items);
            return out.toByteArray();
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to export MediaKit to PDF: " + e.getMessage());
        }
    }

}
