package com.soodora.api.playlist;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PlaylistFullDTO;
import com.soodora.domain.user.UserDTO;
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
            @ApiResponse(responseCode = "400", description = "Invalid request or playlist already exists", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping
    ResponseEntity<JsonResponse> createPlaylist(
            @AuthenticationPrincipal UserDTO user,
            @RequestPart("data") CreatePlaylistRequest createPlaylistRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Search playlists with optional filters")
    @GetMapping("/search")
    Page<PlaylistDTO> searchPlaylists(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable);

    @Operation(summary = "Get a playlist by its slug")
    @GetMapping("/{slug}")
    PlaylistFullDTO getPlaylist(
            @PathVariable String slug,
            Pageable pageableAudioPosts);

    @Operation(summary = "Update a playlist")
    @PutMapping("/{slug}")
    ResponseEntity<JsonResponse> updatePlaylist(@PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Delete a playlist by slug")
    @DeleteMapping("/{slug}")
    void deletePlaylist(@PathVariable("slug") String slug);

    @Operation(summary = "Add a post to a playlist")
    @PostMapping("/{playlistSlug}/posts/{postSlug}")
    ResponseEntity<JsonResponse> addPostToPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts);

    @Operation(summary = "Remove a post from a playlist")
    @DeleteMapping("/{playlistSlug}/posts/{postSlug}")
    ResponseEntity<JsonResponse> removePostFromPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts);

}
