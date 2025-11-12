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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/mediakits")
@RequiredArgsConstructor
public class MediaKitController implements MediaKitResource {

    private final MediaKitService mediaKitService;
    private final FileDataService fileDataService;

    @Override
    @PostMapping
    public ResponseEntity<JsonResponse> createMediaKit(
            @AuthenticationPrincipal User user, @RequestBody CreateMediaKitRequest request) {
        try {
            MediaKit mediaKit = mediaKitService.createMediaKit(request.toCommand(), user);

            return new ResponseEntity<>(MediaKitDTO.build(mediaKit), CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateMediaKit(
            @PathVariable String slug,
            @RequestBody UpdateMediaKitRequest request) {
        try {
            MediaKit mediaKit = mediaKitService.updateMediaKit(request.toCommand(), slug);

            return new ResponseEntity<>(MediaKitDTO.build(mediaKit), OK);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    public void deleteMediaKit(@PathVariable String slug) {
        mediaKitService.deleteMediaKit(slug);
    }

    @Override
    @PostMapping(value = "/{artistSlug}/items", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonResponse> addItem(
            @PathVariable String artistSlug,
            @RequestPart("data") CreateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            MediaKitItem mediaKitItem = mediaKitService.addMediaKitItem(request.toCommand(), artistSlug, fileData);

            return new ResponseEntity<>(MediaKitItemDTO.build(mediaKitItem), CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PutMapping(value = "/items/{itemSlug}", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonResponse> updateItem(
            @PathVariable String itemSlug,
            @RequestPart("data") UpdateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            MediaKitItem mediaKitItem = mediaKitService.updateMediaKitItem(request.toCommand(), itemSlug, fileData);

            return new ResponseEntity<>(MediaKitItemDTO.build(mediaKitItem), OK);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/items/{itemSlug}")
    public void deleteItem(@PathVariable String itemSlug) {
        mediaKitService.deleteMediaKitItem(itemSlug);
    }

    @Override
    @GetMapping("/{slug}")
    public MediaKitDTO getMediaKitBySlug(@PathVariable String slug) {
        return MediaKitDTO.build(mediaKitService.retrieveMediaKitBySlug(slug));
    }

    @Override
    @GetMapping("/artist/{artistSlug}")
    public MediaKitDTO getMediaKitByArtistSlug(@PathVariable String artistSlug) {
        return MediaKitDTO.build(mediaKitService.retrieveMediaKitByArtistSlug(artistSlug));
    }

    @Override
    @GetMapping("/{slug}/export/pdf")
    public ResponseEntity<?> exportPdf(@AuthenticationPrincipal User user, @PathVariable String slug) {
        try {
            byte[] pdfBytes = mediaKitService.exportMediaKitToPdf(slug, user);

            return ok()
                    .header(CONTENT_DISPOSITION, "attachment; filename=\"mediakit_" + slug + ".pdf\"")
                    .contentType(APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse("Failed to export PDF: " + e.getMessage()),
                    INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @GetMapping("/{slug}/analytics")
    public MediaKitAnalyticsDTO getMediaKitAnalytics(@AuthenticationPrincipal User user, @PathVariable String slug) {
        MediaKit mediaKit = mediaKitService.retrieveMediaKitBySlug(slug);

        if (mediaKit.getOwner().getSlug().equals(user.getSlug()) || user.isAdmin())
            return MediaKitAnalyticsDTO.build(mediaKit);
        throw new InvalidPermissionException("Only the media kit owner can access the analytics");
    }

}
