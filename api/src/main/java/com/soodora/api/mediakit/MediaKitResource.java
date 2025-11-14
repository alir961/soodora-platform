package com.soodora.api.mediakit;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.user.User;
import com.soodora.service.mediakit.CreateMediaKitItemRequest;
import com.soodora.service.mediakit.CreateMediaKitRequest;
import com.soodora.service.mediakit.UpdateMediaKitItemRequest;
import com.soodora.service.mediakit.UpdateMediaKitRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Media Kit Controller", description = "Endpoints to manage artist media kits and their items")
public interface MediaKitResource {

    @Operation(summary = "Create a new media kit", description = "Creates a new media kit for the authenticated artist. The user must have the role 'ARTIST'.", responses = {
            @ApiResponse(responseCode = "201", description = "Media kit successfully created", content = @Content(schema = @Schema(implementation = JsonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid media kit data", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> createMediaKit(
            @Parameter(hidden = true) User user,
            @RequestBody(required = true, description = "The data required to create a new media kit") CreateMediaKitRequest request);

    @Operation(summary = "Update an existing media kit", description = "Updates the media kit identified by its slug. Only the owner artist can update it.", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid update data", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Media kit not found")
    })
    ResponseEntity<JsonResponse> updateMediaKit(
            @Parameter(description = "Slug of the media kit to update", required = true) @PathVariable String slug,
            @RequestBody(required = true, description = "Updated media kit information") UpdateMediaKitRequest request);

    @Operation(summary = "Delete a media kit", description = "Deletes a media kit by its slug. Only the owner artist can delete it.", responses = {
            @ApiResponse(responseCode = "204", description = "Media kit successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Media kit not found")
    })
    void deleteMediaKit(
            @Parameter(description = "Slug of the media kit to delete", required = true) @PathVariable String slug);

    @Operation(summary = "Add an item to a media kit", description = "Adds a new item (photo, video, press link, etc.) to an artist's media kit. Accepts multipart form-data with an optional file.", responses = {
            @ApiResponse(responseCode = "201", description = "Item successfully added", content = @Content(schema = @Schema(implementation = JsonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid item data", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> addItem(
            @Parameter(description = "Slug of the artist owning the media kit", required = true) @PathVariable String artistSlug,
            @RequestPart("data") CreateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Update an item in a media kit", description = "Updates a media kit item by its slug. Accepts multipart form-data with an optional replacement file.", responses = {
            @ApiResponse(responseCode = "200", description = "Item successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid item update data", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Media kit item not found")
    })
    ResponseEntity<JsonResponse> updateItem(
            @Parameter(description = "Slug of the media kit item to update", required = true) @PathVariable String itemSlug,
            @RequestPart("data") UpdateMediaKitItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Delete an item from a media kit", description = "Deletes a media kit item by its slug.", responses = {
            @ApiResponse(responseCode = "204", description = "Item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    void deleteItem(
            @Parameter(description = "Slug of the item to delete", required = true) @PathVariable String itemSlug);

    @Operation(summary = "Retrieve a media kit by slug", description = "Fetches a media kit by its unique slug identifier.", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit successfully retrieved", content = @Content(schema = @Schema(implementation = MediaKitDTO.class))),
            @ApiResponse(responseCode = "404", description = "Media kit not found")
    })
    MediaKitDTO getMediaKitBySlug(
            @Parameter(description = "Slug of the media kit", required = true) @PathVariable String slug);

    @Operation(summary = "Retrieve a media kit by artist slug", description = "Fetches the media kit associated with a specific artist.", responses = {
            @ApiResponse(responseCode = "200", description = "Media kit successfully retrieved", content = @Content(schema = @Schema(implementation = MediaKitDTO.class))),
            @ApiResponse(responseCode = "404", description = "Media kit not found")
    })
    MediaKitDTO getMediaKitByArtistSlug(
            @Parameter(description = "Slug of the artist", required = true) @PathVariable String artistSlug);

    @Operation(summary = "Export a media kit as PDF", description = "Exports the media kit as a downloadable PDF. The user must have permission to access it.", responses = {
            @ApiResponse(responseCode = "200", description = "PDF successfully exported", content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "403", description = "Forbidden — user not allowed to access this media kit"),
            @ApiResponse(responseCode = "500", description = "Internal server error during PDF export", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<?> exportPdf(
            @Parameter(hidden = true) User user,
            @Parameter(description = "Slug of the media kit to export", required = true) @PathVariable String slug);

    @Operation(summary = "Get analytics for a media kit", description = "Returns analytics for the specified media kit. Only the owner or an admin is allowed to access this endpoint.", responses = {
            @ApiResponse(responseCode = "200", description = "Analytics successfully retrieved", content = @Content(schema = @Schema(implementation = MediaKitAnalyticsDTO.class))),
            @ApiResponse(responseCode = "403", description = "User does not have permission to access these analytics"),
            @ApiResponse(responseCode = "404", description = "Media kit not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    MediaKitAnalyticsDTO getMediaKitAnalytics(@AuthenticationPrincipal User user, @PathVariable String slug);

}
