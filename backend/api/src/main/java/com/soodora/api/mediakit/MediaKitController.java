package com.soodora.api.mediakit;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.mediakit.*;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.shared.InvalidPermissionException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/media-kits")
@RequiredArgsConstructor
public class MediaKitController implements MediaKitResource {

    private final MediaKitService mediaKitService;
    private final FileDataService fileDataService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> createMediaKit(
            @AuthenticationPrincipal User user, @RequestBody CreateMediaKitRequest request) {
        try {
            MediaKit mediaKit = mediaKitService.createMediaKit(user, request.toCommand());

            return new ResponseEntity<>(MediaKitDTO.build(mediaKit), CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping("/{slug}")
    public MediaKitDTO getMediaKitBySlug(@PathVariable String slug) {
        return MediaKitDTO.build(mediaKitService.retrieveMediaKitBySlug(slug));
    }

    @Override
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> updateMediaKit(
            @AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestBody UpdateMediaKitRequest request) {
        try {
            MediaKit mediaKit = mediaKitService.updateMediaKit(user, slug, request.toCommand());

            return new ResponseEntity<>(MediaKitDTO.build(mediaKit), OK);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deleteMediaKit(@AuthenticationPrincipal User user, @PathVariable String slug) {
        mediaKitService.deleteMediaKit(user, slug);
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping(value = "/{artistSlug}/items", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonResponse> addMediaKitItem(
            @AuthenticationPrincipal User user,
            @PathVariable String artistSlug,
            @RequestPart("data") CreateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            MediaKitItem mediaKitItem = mediaKitService.addMediaKitItem(user, artistSlug, request.toCommand(),
                    fileData);

            return new ResponseEntity<>(MediaKitItemDTO.build(mediaKitItem), CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping(value = "/items/{itemSlug}", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonResponse> updateMediaKitItem(
            @AuthenticationPrincipal User user,
            @PathVariable String itemSlug,
            @RequestPart("data") UpdateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            MediaKitItem mediaKitItem = mediaKitService.updateMediaKitItem(user, itemSlug, request.toCommand(),
                    fileData);

            return new ResponseEntity<>(MediaKitItemDTO.build(mediaKitItem), OK);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/items/{itemSlug}")
    public void deleteMediaKitItem(@AuthenticationPrincipal User user, @PathVariable String itemSlug) {
        mediaKitService.deleteMediaKitItem(user, itemSlug);
    }

    @Override
    @GetMapping("/{slug}/export/pdf")
    public ResponseEntity<?> exportPdf(@AuthenticationPrincipal User user, @PathVariable String slug) {
        try {
            byte[] pdfBytes = mediaKitService.exportMediaKitToPdf(user, slug);

            return ok()
                    .header(CONTENT_DISPOSITION, "attachment; filename=\"mediakit_" + slug + ".pdf\"")
                    .contentType(APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (InvalidPermissionException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), FORBIDDEN);
        } catch (UnknownMediaKitException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        } catch (Exception exception) {
            ExceptionResponse exceptionResponse = new ExceptionResponse(
                    "Failed to export PDF: " + exception.getMessage());

            return new ResponseEntity<>(exceptionResponse, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/{slug}/analytics")
    public MediaKitAnalyticsDTO getMediaKitAnalytics(@AuthenticationPrincipal User user, @PathVariable String slug) {
        MediaKit mediaKit = mediaKitService.retrieveMediaKitBySlug(slug);

        if (mediaKit.hasAccess(user))
            return MediaKitAnalyticsDTO.build(mediaKit);
        throw new InvalidPermissionException("Only the media kit owner can access the analytics");
    }

    @Override
    @GetMapping("/artist/{artistSlug}")
    public MediaKitDTO getMediaKitByArtistSlug(@PathVariable String artistSlug) {
        // TODO: Move to ArtistController
        return MediaKitDTO.build(mediaKitService.retrieveMediaKitByArtistSlug(artistSlug));
    }

}
