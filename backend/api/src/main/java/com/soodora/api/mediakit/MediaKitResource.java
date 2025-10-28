package com.soodora.api.mediakit;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.mediakit.CreateMediaKitItemRequest;
import com.soodora.service.mediakit.CreateMediaKitRequest;
import com.soodora.service.mediakit.UpdateMediaKitItemRequest;
import com.soodora.service.mediakit.UpdateMediaKitRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Media Kit Controller")
@SecurityRequirement(name = "bearerAuth")
public interface MediaKitResource {

    @Operation(summary = "Create media kit", responses = {
            @ApiResponse(responseCode = "201", description = "Media kit created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<JsonResponse> createMediaKit(
            @RequestBody(required = true) CreateMediaKitRequest request,
            UserDTO user);

    @Operation(summary = "Update media kit", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<JsonResponse> updateMediaKit(
            String slug,
            @RequestBody(required = true) UpdateMediaKitRequest request);

    @Operation(summary = "Delete media kit", responses = {
            @ApiResponse(responseCode = "204", description = "Media kit deleted")
    })
    void deleteMediaKit(String slug);

    @Operation(summary = "Add item to media kit", responses = {
            @ApiResponse(responseCode = "201", description = "Item added"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<JsonResponse> addItem(
            String artistSlug,
            @RequestPart("data") CreateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Update item in media kit", responses = {
            @ApiResponse(responseCode = "200", description = "Item updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<JsonResponse> updateItem(
            String itemSlug,
            @RequestPart("data") UpdateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Delete item from media kit", responses = {
            @ApiResponse(responseCode = "204", description = "Item deleted")
    })
    void deleteItem(String itemSlug);

    @Operation(summary = "Get media kit by slug", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit retrieved")
    })
    MediaKitDTO getMediaKitBySlug(String slug);

    @Operation(summary = "Get media kit by artist slug", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit retrieved")
    })
    MediaKitDTO getMediaKitByArtistSlug(String artistSlug);

    @Operation(summary = "Export media kit as PDF", responses = {
            @ApiResponse(responseCode = "200", description = "PDF exported"),
            @ApiResponse(responseCode = "500", description = "Failed to export PDF")
    })
    ResponseEntity<?> exportPdf(String slug);

}
