package com.soodora.api.mediakit;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakit.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.mediakit.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/mediakits")
@Tag(name = "Media Kit Controller")
@RequiredArgsConstructor
public class MediaKitController {

    private final MediaKitService mediaKitService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> createMediaKit(
            @RequestBody CreateMediaKitRequest request,
            @AuthenticationPrincipal UserDTO user) {
        try {
            MediaKitDTO mediaKitDTO = mediaKitService.createMediaKit(request.toCommand(), user);
            return new ResponseEntity<>(mediaKitDTO, CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{slug}")
    public ResponseEntity<?> updateMediaKit(
            @PathVariable String slug,
            @RequestBody UpdateMediaKitRequest request) {
        try {
            MediaKitDTO mediaKitDTO = mediaKitService.updateMediaKit(request.toCommand(), slug);
            return new ResponseEntity<>(mediaKitDTO, ACCEPTED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteMediaKit(@PathVariable String slug) {
        mediaKitService.deleteMediaKit(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{artistSlug}/items")
    public ResponseEntity<JsonResponse> addItem(@PathVariable String artistSlug,
            @RequestBody CreateMediaKitItemRequest request) {
        try {
            mediaKitService.addMediaKitItem(request.toCommand(), artistSlug);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/items/{itemSlug}")
    public ResponseEntity<?> updateItem(@PathVariable String itemSlug, @RequestBody UpdateMediaKitItemRequest request) {
        try {
            mediaKitService.updateMediaKitItem(request.toCommand(), itemSlug);
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/items/{itemSlug}")
    public void deleteItem(@PathVariable String itemSlug) {
        mediaKitService.deleteMediaKitItem(itemSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public MediaKitDTO getMediaKitBySlug(@PathVariable String slug) {
        return mediaKitService.retrieveMediaKitBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/artist/{artistSlug}")
    public MediaKitDTO getMediaKitByArtistSlug(@PathVariable String artistSlug) {
        return mediaKitService.retrieveMediaKitByArtistSlug(artistSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}/export/pdf")
    public ResponseEntity<?> exportPdf(@PathVariable String slug) {
        try {
            byte[] pdfBytes = mediaKitService.exportMediaKitToPdf(slug);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mediakit_" + slug + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export PDF: " + e.getMessage());
        }
    }
}
