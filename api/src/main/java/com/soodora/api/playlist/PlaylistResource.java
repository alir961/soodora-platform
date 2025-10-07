package com.soodora.api.playlist;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PlaylistFullDTO;
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
@SecurityRequirement(name = "bearerAuth")
public interface PlaylistResource {

    @Operation(summary = "Create a new playlist", responses = {
            @ApiResponse(responseCode = "201", description = "Playlist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or playlist already exists", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<JsonResponse> createPlaylist(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreatePlaylistRequest createPlaylistRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Search playlists with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "Playlists retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    Page<PlaylistDTO> searchPlaylists(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable);

    @Operation(summary = "Get a playlist by its slug", responses = {
            @ApiResponse(responseCode = "200", description = "Playlist retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    PlaylistFullDTO getPlaylist(
            @PathVariable String slug,
            Pageable pageableAudioPosts);

    @Operation(summary = "Update a playlist", responses = {
            @ApiResponse(responseCode = "200", description = "Playlist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> updatePlaylist(
            @PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Delete a playlist by slug", responses = {
            @ApiResponse(responseCode = "204", description = "Playlist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    void deletePlaylist(@PathVariable("slug") String slug);

    @Operation(summary = "Add a post to a playlist", responses = {
            @ApiResponse(responseCode = "201", description = "Post added to playlist"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Playlist or post not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> addPostToPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts);

    @Operation(summary = "Remove a post from a playlist", responses = {
            @ApiResponse(responseCode = "200", description = "Post removed from playlist"),
            @ApiResponse(responseCode = "404", description = "Playlist or post not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> removePostFromPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts);

}
