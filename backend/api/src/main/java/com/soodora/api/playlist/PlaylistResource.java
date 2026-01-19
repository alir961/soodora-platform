package com.soodora.api.playlist;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.user.User;
import com.soodora.service.playlist.CreatePlaylistRequest;
import com.soodora.service.playlist.UpdatePlaylistRequest;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Playlist Controller")
public interface PlaylistResource {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new playlist", responses = {
            @ApiResponse(responseCode = "201", description = "Playlist created successfully", content = @Content(schema = @Schema(implementation = PlaylistSlugResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or playlist name already used", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    ResponseEntity<JsonResponse> createPlaylist(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreatePlaylistRequest createPlaylistRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Search playlists with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "Playlists retrieved successfully")
    })
    Page<PlaylistDTO> searchPlaylists(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable);

    @Operation(summary = "Get a playlist by its slug", responses = {
            @ApiResponse(responseCode = "200", description = "Playlist retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden (private playlist)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    PopulatedPlaylistDTO getPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable String slug,
            Pageable pageableAudioPosts);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a playlist", responses = {
            @ApiResponse(responseCode = "200", description = "Playlist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content)
    })
    ResponseEntity<JsonResponse> updatePlaylist(
            @AuthenticationPrincipal User user, @PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a playlist by slug", responses = {
            @ApiResponse(responseCode = "200", description = "Playlist deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content)
    })
    void deletePlaylist(@AuthenticationPrincipal User user, @PathVariable("slug") String slug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a post to a playlist", responses = {
            @ApiResponse(responseCode = "200", description = "Post added to playlist", content = @Content(schema = @Schema(implementation = PopulatedPlaylistDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Playlist or post not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> addPostToPlaylist(
            @AuthenticationPrincipal User user, @PathVariable String playlistSlug,
            @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove a post from a playlist", responses = {
            @ApiResponse(responseCode = "200", description = "Post removed from playlist", content = @Content(schema = @Schema(implementation = PopulatedPlaylistDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Playlist or post not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> removePostFromPlaylist(
            @AuthenticationPrincipal User user, @PathVariable String playlistSlug,
            @PathVariable String postSlug);

}
